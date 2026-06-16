package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommunityPostResponse {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private String title;
    
    private String content;
    
    private String images;
    
    private Integer type;
    
    private Integer viewCount;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer status;
    
    private Boolean isLiked;
    
    private LocalDateTime createdAt;
}