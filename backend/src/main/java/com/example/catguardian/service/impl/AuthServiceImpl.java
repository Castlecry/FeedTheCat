package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.LoginRequest;
import com.example.catguardian.dto.request.RegisterRequest;
import com.example.catguardian.dto.response.LoginResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.service.AuthService;
import com.example.catguardian.service.UserService;
import com.example.catguardian.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    
    @Value("${app.sms.expire-minutes:5}")
    private int smsExpireMinutes;
    
    private static final String SMS_KEY_PREFIX = "sms:code:";
    private static final Random RANDOM = new Random();
    
    @Override
    public LoginResponse register(RegisterRequest request) {
        User user = userService.register(request);
        return generateLoginResponse(user);
    }
    
    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userService.getByPhone(request.getPhone());
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw BusinessException.badRequest("密码错误");
        }
        
        if (user.getStatus() != 1) {
            throw BusinessException.forbidden("账号已被禁用");
        }
        
        return generateLoginResponse(user);
    }
    
    @Override
    public LoginResponse refreshToken(String refreshToken) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(refreshToken);
            User user = userService.getById(userId);
            return generateLoginResponse(user);
        } catch (Exception e) {
            throw BusinessException.unauthorized("无效的刷新Token");
        }
    }
    
    @Override
    public String sendVerificationCode(String phone) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        
        redisTemplate.opsForValue().set(
                SMS_KEY_PREFIX + phone,
                code,
                smsExpireMinutes,
                TimeUnit.MINUTES
        );
        
        log.info("发送验证码: {} -> {}", phone, code);
        return code;
    }
    
    @Override
    public boolean verifyCode(String phone, String code) {
        String storedCode = redisTemplate.opsForValue().get(SMS_KEY_PREFIX + phone);
        return code.equals(storedCode);
    }
    
    private LoginResponse generateLoginResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getId());
        
        return LoginResponse.builder()
                .userId(user.getId())
                .token(accessToken)
                .expireTime(System.currentTimeMillis() + 7200000L)
                .refreshToken(refreshToken)
                .refreshExpireTime(System.currentTimeMillis() + 86400000L)
                .build();
    }
}
