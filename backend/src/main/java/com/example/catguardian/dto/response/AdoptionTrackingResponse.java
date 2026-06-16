package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 领养跟踪响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionTrackingResponse {
    
    private Long id;
    
    private Long applicationId;
    
    private LocalDateTime trackingTime;
    
    private Integer status;
    
    private String statusDescription;
    
    private String notes;
    
    private List<String> photos;
    
    private LocalDateTime createdAt;
}