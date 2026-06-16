package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 用户信息响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    
    private Long id;
    
    private String phone;
    
    private String name;
    
    private String avatar;
    
    private String address;
    
    private Integer role;
    
    private String roleDescription;
    
    private Integer creditScore;
    
    private Integer pointsBalance;
}
