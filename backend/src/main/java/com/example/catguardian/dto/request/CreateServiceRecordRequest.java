package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建服务记录请求DTO
 */
@Data
public class CreateServiceRecordRequest {
    
    @NotNull(message = "服务时间不能为空")
    private LocalDateTime serviceTime;
    
    private String videoUrl;
    
    private String lockPhotoUrl;
    
    private String notes;
}