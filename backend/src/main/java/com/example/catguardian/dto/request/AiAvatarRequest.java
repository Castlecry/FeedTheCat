package com.example.catguardian.dto.request;

import lombok.Data;

/**
 * AI头像生成请求DTO
 */
@Data
public class AiAvatarRequest {
    
    /**
     * 头像风格：cute, realistic, cartoon, pixel, watercolor
     */
    private String style;
}
