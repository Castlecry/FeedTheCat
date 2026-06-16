package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.RegisterRequest;
import com.example.catguardian.dto.request.RealNameAuthRequest;
import com.example.catguardian.dto.response.RealNameAuthResponse;
import com.example.catguardian.dto.response.UserProfileResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.entity.UserPoints;
import com.example.catguardian.enums.UserRole;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.UserPointsRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final UserPointsRepository userPointsRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw BusinessException.badRequest("该手机号已注册");
        }
        
        User user = User.builder()
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .idCard(request.getIdCard())
                .role(UserRole.NORMAL.getCode())
                .creditScore(100)
                .status(1)
                .build();
        
        User savedUser = userRepository.save(user);
        
        log.info("用户注册成功: {}", savedUser.getId());
        return savedUser;
    }
    
    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
    }
    
    @Override
    public User getByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> BusinessException.notFound("用户不存在"));
    }
    
    @Override
    @Transactional
    public User update(Long id, User user) {
        User existingUser = getById(id);
        
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        
        return userRepository.save(existingUser);
    }
    
    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long id, User user) {
        update(id, user);
        return getProfile(id);
    }
    
    @Override
    public UserProfileResponse getProfile(Long userId) {
        User user = getById(userId);
        
        Integer pointsBalance = userPointsRepository.findByUserId(userId)
                .map(UserPoints::getBalance)
                .orElse(0);
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .name(user.getName())
                .avatar(user.getAvatar())
                .address(user.getAddress())
                .role(user.getRole())
                .roleDescription(UserRole.fromCode(user.getRole()).getDescription())
                .creditScore(user.getCreditScore())
                .pointsBalance(pointsBalance)
                .build();
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    
    @Override
    @Transactional
    public User updateRole(Long userId, Integer role) {
        User user = getById(userId);
        user.setRole(role);
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public RealNameAuthResponse realNameAuth(Long userId, RealNameAuthRequest request) {
        log.info("开始处理实名认证请求，用户ID: {}", userId);
        
        User user = getById(userId);
        log.info("用户查询成功，用户信息: {}", user.getId());
        
        Integer authStatus = user.getAuthStatus();
        log.info("当前认证状态: {}", authStatus);
        
        if (authStatus == null) {
            authStatus = 0;
            user.setAuthStatus(0);
            log.info("认证状态为null，已设置为0");
        }
        
        if (authStatus == 1) {
            throw BusinessException.badRequest("您已完成实名认证");
        }
        
        user.setRealName(request.getRealName());
        user.setIdCard(request.getIdCard());
        user.setFaceImage(request.getFaceImage());
        user.setAuthStatus(0);
        user.setAuthRejectReason(null);
        user.setAuthTime(null);
        
        log.info("用户信息已更新，准备保存");
        
        User savedUser = userRepository.save(user);
        log.info("用户信息保存成功，用户ID: {}", savedUser.getId());
        
        RealNameAuthResponse response = convertToAuthResponse(savedUser);
        log.info("转换响应成功");
        
        return response;
    }
    
    @Override
    public RealNameAuthResponse getRealNameAuthStatus(Long userId) {
        User user = getById(userId);
        return convertToAuthResponse(user);
    }
    
    private RealNameAuthResponse convertToAuthResponse(User user) {
        Integer authStatus = user.getAuthStatus();
        if (authStatus == null) {
            authStatus = 0;
        }
        
        String statusDescription;
        switch (authStatus) {
            case 0:
                statusDescription = "待审核";
                break;
            case 1:
                statusDescription = "已通过";
                break;
            case 2:
                statusDescription = "已拒绝";
                break;
            default:
                statusDescription = "未认证";
        }
        
        return RealNameAuthResponse.builder()
                .userId(user.getId())
                .realName(user.getRealName())
                .idCard(maskIdCard(user.getIdCard()))
                .status(authStatus)
                .statusDescription(statusDescription)
                .rejectReason(user.getAuthRejectReason())
                .authenticatedAt(user.getAuthTime())
                .build();
    }
    
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 18) {
            return "";
        }
        return idCard.substring(0, 3) + "***********" + idCard.substring(14);
    }
}
