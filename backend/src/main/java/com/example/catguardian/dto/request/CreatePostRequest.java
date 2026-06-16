package com.example.catguardian.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 发布帖子请求DTO
 */
@Data
public class CreatePostRequest {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private List<String> images;
    
    private Integer type;
}
