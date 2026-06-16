package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.CreateTnrApplicationRequest;
import com.example.catguardian.dto.response.TnrApplicationResponse;
import com.example.catguardian.entity.TnrApplication;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.TnrApplicationRepository;
import com.example.catguardian.service.TnrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TnrServiceImpl implements TnrService {
    
    private final TnrApplicationRepository tnrApplicationRepository;
    
    @Override
    @Transactional
    public TnrApplicationResponse createApplication(Long userId, CreateTnrApplicationRequest request) {
        TnrApplication application = TnrApplication.builder()
                .userId(userId)
                .hospitalId(request.getHospitalId())
                .catName(request.getCatName())
                .location(request.getLocation())
                .description(request.getDescription())
                .photos(request.getPhotos())
                .operationTime(request.getOperationTime())
                .status(0)
                .build();
        
        TnrApplication saved = tnrApplicationRepository.save(application);
        log.info("TNR申请创建成功，申请ID: {}", saved.getId());
        return convertToResponse(saved);
    }
    
    @Override
    public List<TnrApplicationResponse> getApplications() {
        return tnrApplicationRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public TnrApplicationResponse getApplication(Long id) {
        TnrApplication application = tnrApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("TNR申请不存在"));
        return convertToResponse(application);
    }
    
    @Override
    @Transactional
    public void approveApplication(Long id) {
        TnrApplication application = tnrApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("TNR申请不存在"));
        
        application.setStatus(1);
        tnrApplicationRepository.save(application);
        log.info("TNR申请 {} 已通过", id);
    }
    
    @Override
    @Transactional
    public void rejectApplication(Long id) {
        TnrApplication application = tnrApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("TNR申请不存在"));
        
        application.setStatus(2);
        tnrApplicationRepository.save(application);
        log.info("TNR申请 {} 已拒绝", id);
    }
    
    @Override
    @Transactional
    public void rejectApplication(Long id, String reason) {
        TnrApplication application = tnrApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("TNR申请不存在"));
        
        application.setStatus(2);
        application.setRejectReason(reason);
        tnrApplicationRepository.save(application);
        log.info("TNR申请 {} 已拒绝，原因: {}", id, reason);
    }
    
    @Override
    @Transactional
    public TnrApplicationResponse uploadOperationPhotos(Long id, String photos) {
        TnrApplication application = tnrApplicationRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("TNR申请不存在"));
        
        if (application.getStatus() != 1) {
            throw BusinessException.badRequest("申请未通过，无法上传手术照片");
        }
        
        application.setPhotos(photos);
        application.setStatus(3);
        TnrApplication saved = tnrApplicationRepository.save(application);
        log.info("TNR申请 {} 手术照片上传成功", id);
        return convertToResponse(saved);
    }
    
    private TnrApplicationResponse convertToResponse(TnrApplication application) {
        return TnrApplicationResponse.builder()
                .id(application.getId())
                .userId(application.getUserId())
                .hospitalId(application.getHospitalId())
                .catName(application.getCatName())
                .location(application.getLocation())
                .description(application.getDescription())
                .photos(application.getPhotos())
                .status(application.getStatus())
                .rejectReason(application.getRejectReason())
                .operationTime(application.getOperationTime())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}