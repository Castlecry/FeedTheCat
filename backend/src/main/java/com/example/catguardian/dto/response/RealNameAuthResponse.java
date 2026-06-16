package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RealNameAuthResponse {
    
    private Long userId;
    
    private String realName;
    
    private String idCard;
    
    private Integer status;
    
    private String statusDescription;
    
    private String rejectReason;
    
    private LocalDateTime authenticatedAt;
}