package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {
    
    private Long id;
    
    private Long postId;
    
    private Long userId;
    
    private String userName;
    
    private String userAvatar;
    
    private String content;
    
    private Long parentId;
    
    private LocalDateTime createdAt;
}