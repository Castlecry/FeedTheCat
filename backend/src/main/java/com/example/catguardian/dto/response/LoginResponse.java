package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 登录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private Long userId;
    
    private String token;
    
    private Long expireTime;
    
    private String refreshToken;
    
    private Long refreshExpireTime;
}
