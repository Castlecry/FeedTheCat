package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * AI头像生成响应DTO
 */
@Data
@Builder
public class AiAvatarResponse {
    
    private String url;
    
    private String style;
}
