package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 服务记录响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecordResponse {
    
    private Long id;
    
    private Long orderId;
    
    private LocalDateTime serviceTime;
    
    private String videoUrl;
    
    private String lockPhotoUrl;
    
    private String notes;
    
    private LocalDateTime createdAt;
}