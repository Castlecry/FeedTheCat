package com.example.catguardian.service;

import com.example.catguardian.dto.request.RegisterRequest;
import com.example.catguardian.dto.request.RealNameAuthRequest;
import com.example.catguardian.dto.response.RealNameAuthResponse;
import com.example.catguardian.dto.response.UserProfileResponse;
import com.example.catguardian.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户注册
     */
    User register(RegisterRequest request);
    
    /**
     * 根据ID获取用户
     */
    User getById(Long id);
    
    /**
     * 根据手机号获取用户
     */
    User getByPhone(String phone);
    
    /**
     * 更新用户信息
     */
    User update(Long id, User user);
    
    /**
     * 更新用户资料并返回安全响应
     */
    UserProfileResponse updateProfile(Long id, User user);
    
    /**
     * 获取用户信息
     */
    UserProfileResponse getProfile(Long userId);
    
    /**
     * 检查用户是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 更新用户角色
     */
    User updateRole(Long userId, Integer role);
    
    /**
     * 实名认证
     */
    RealNameAuthResponse realNameAuth(Long userId, RealNameAuthRequest request);
    
    /**
     * 获取实名认证状态
     */
    RealNameAuthResponse getRealNameAuthStatus(Long userId);
}
