package com.example.catguardian.service;

import com.example.catguardian.dto.request.AiAdviceRequest;
import com.example.catguardian.dto.request.AiAvatarRequest;
import com.example.catguardian.dto.response.AiAdviceResponse;
import com.example.catguardian.dto.response.AiAvatarResponse;
import com.example.catguardian.dto.response.AiQuoteResponse;

/**
 * AI服务接口
 * 
 * 当前实现为Mock模式，后续接入真实AI API时替换实现类即可
 */
public interface AiService {
    
    /**
     * 生成猫咪头像
     */
    AiAvatarResponse generateAvatar(Long userId, AiAvatarRequest request);
    
    /**
     * 生成猫咪语录
     */
    AiQuoteResponse generateQuote(Long userId);
    
    /**
     * 生成饲养建议
     */
    AiAdviceResponse generateAdvice(Long userId, AiAdviceRequest request);
    
    /**
     * 获取今日AI使用次数
     */
    int getTodayUsage(Long userId);
}
