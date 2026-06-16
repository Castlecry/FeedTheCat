package com.example.catguardian.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 社区任务响应DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityTaskResponse {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private String title;
    
    private String content;
    
    private Integer type;
    
    private String typeDescription;
    
    private String location;
    
    private Integer rewardPoints;
    
    private Integer status;
    
    private String statusDescription;
    
    private Long assigneeId;
    
    private String assigneeName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}