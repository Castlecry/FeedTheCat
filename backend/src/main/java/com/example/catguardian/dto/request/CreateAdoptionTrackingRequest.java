package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建领养跟踪记录请求DTO
 */
@Data
public class CreateAdoptionTrackingRequest {
    
    @NotNull(message = "猫咪状态不能为空")
    private Integer status;
    
    private String notes;
    
    private List<String> photos;
    
    private LocalDateTime trackingTime;
}