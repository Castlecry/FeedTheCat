package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建TNR申请请求DTO
 */
@Data
public class CreateTnrApplicationRequest {
    
    private String catName;
    
    @NotBlank(message = "发现地点不能为空")
    @Size(max = 200, message = "地点长度不能超过200字")
    private String location;
    
    private String description;
    
    private String photos;
    
    private Long hospitalId;
    
    private LocalDateTime operationTime;
}
