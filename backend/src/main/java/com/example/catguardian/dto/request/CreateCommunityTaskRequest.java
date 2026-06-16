package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommunityTaskRequest {
    
    @NotBlank(message = "任务标题不能为空")
    private String title;
    
    private String content;
    
    private Integer type;
    
    private String location;
    
    private Integer rewardPoints;
}