package com.example.catguardian.service;

import com.example.catguardian.dto.request.LoginRequest;
import com.example.catguardian.dto.request.RegisterRequest;
import com.example.catguardian.dto.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户注册
     */
    LoginResponse register(RegisterRequest request);
    
    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 刷新Token
     */
    LoginResponse refreshToken(String refreshToken);
    
    /**
     * 发送验证码
     */
    String sendVerificationCode(String phone);
    
    /**
     * 验证验证码
     */
    boolean verifyCode(String phone, String code);
}
