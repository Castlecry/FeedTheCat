package com.example.catguardian.dto.response;

import lombok.Builder;
import lombok.Data;

/**
 * AI头像生成响应DTO
 */
@Data
@Builder
public class AiAvatarResponse {

    /**
     * 用户对头像的描述
     */
    private String prompt;

    /**
     * 生成的头像风格
     */
    private String style;

    /**
     * 生成的头像URL
     */
    private String url;
}
