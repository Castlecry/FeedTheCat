package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.ServiceProviderApplyRequest;
import com.example.catguardian.entity.ServiceProviderCredential;
import com.example.catguardian.entity.User;
import com.example.catguardian.enums.UserRole;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.ServiceProviderCredentialRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.ServiceProviderCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceProviderCredentialServiceImpl implements ServiceProviderCredentialService {
    
    private final ServiceProviderCredentialRepository credentialRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public ServiceProviderCredential apply(Long userId, ServiceProviderApplyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
        
        if (user.getAuthStatus() != 1) {
            throw BusinessException.badRequest("请先完成实名认证");
        }
        
        ServiceProviderCredential existing = credentialRepository.findByUserId(userId).orElse(null);
        if (existing != null && existing.getStatus() == 1) {
            throw BusinessException.badRequest("您已通过服务方资质审核");
        }
        
        ServiceProviderCredential credentials = ServiceProviderCredential.builder()
                .userId(userId)
                .idCardFront(request.getIdCardFront())
                .idCardBack(request.getIdCardBack())
                .criminalRecord(request.getCriminalRecord())
                .trainingCertificate(request.getTrainingCertificate())
                .hasSignedAgreement(request.getHasSignedAgreement() != null ? request.getHasSignedAgreement() : 0)
                .status(0)
                .build();
        
        ServiceProviderCredential saved = credentialRepository.save(credentials);
        log.info("用户 {} 提交服务方资质申请", userId);
        return saved;
    }
    
    @Override
    public ServiceProviderCredential getByUserId(Long userId) {
        return credentialRepository.findByUserId(userId)
                .orElseThrow(() -> BusinessException.notFound("资质申请不存在"));
    }
    
    @Override
    public ServiceProviderCredential getById(Long id) {
        return credentialRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("资质申请不存在"));
    }
    
    @Override
    public List<ServiceProviderCredential> getPendingApplications() {
        return credentialRepository.findByStatus(0);
    }
    
    @Override
    @Transactional
    public ServiceProviderCredential approve(Long id) {
        ServiceProviderCredential credentials = getById(id);
        
        if (credentials.getStatus() != 0) {
            throw BusinessException.badRequest("申请状态不正确");
        }
        
        credentials.setStatus(1);
        ServiceProviderCredential saved = credentialRepository.save(credentials);
        
        User user = userRepository.findById(credentials.getUserId()).orElse(null);
        if (user != null) {
            user.setRole(UserRole.SERVICE_PROVIDER.getCode());
            userRepository.save(user);
            log.info("用户 {} 资质审核通过，已升级为服务方", credentials.getUserId());
        }
        
        return saved;
    }
    
    @Override
    @Transactional
    public ServiceProviderCredential reject(Long id, String reason) {
        ServiceProviderCredential credentials = getById(id);
        
        if (credentials.getStatus() != 0) {
            throw BusinessException.badRequest("申请状态不正确");
        }
        
        credentials.setStatus(2);
        credentials.setRejectReason(reason);
        
        log.info("用户 {} 资质审核被拒绝: {}", credentials.getUserId(), reason);
        return credentialRepository.save(credentials);
    }
}