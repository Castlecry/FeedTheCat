package com.example.catguardian.service.impl;

import com.example.catguardian.dto.request.AiAdviceRequest;
import com.example.catguardian.dto.request.AiAvatarRequest;
import com.example.catguardian.dto.response.AiAdviceResponse;
import com.example.catguardian.dto.response.AiAvatarResponse;
import com.example.catguardian.dto.response.AiQuoteResponse;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.service.AiService;
import com.example.catguardian.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * AI服务实现类 - Mock模式
 * 
 * 当前使用预设数据模拟AI生成结果。
 * 后续接入真实AI API时，只需替换本实现类中的调用逻辑即可。
 * 
 * 需要在application.yml中配置：
 * ai:
 *   api-key:        # AI服务API Key（如OpenAI、百度文心等）
 *   api-url:        # AI服务API地址
 *   model:          # 使用的模型名称
 *   daily-limit: 5  # 每日使用次数限制
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {
    
    private final PointsService pointsService;
    
    private static final int DAILY_LIMIT = 5;
    
    private static final String[] AVATAR_STYLES = {"cute", "realistic", "cartoon", "pixel", "watercolor"};
    private static final String[] QUOTES = {
            "喵星人统治地球只是时间问题~",
            "今天也要元气满满地卖萌！",
            "小鱼干是世界上最美味的食物！",
            "睡觉是猫咪的天职",
            "人类，给朕跪下！",
            "咕噜咕噜，表示满意~",
            "晒太阳是每天最重要的事",
            "毛线球是天敌！"
    };
    private static final String[] ADVICE_TOPICS = {"diet", "health", "behavior", "environment", "play"};
    private static final String[] ADVICE_CONTENTS = {
            "建议每天定时喂食，保持饮食规律",
            "定期带猫咪去医院做体检",
            "可以使用逗猫棒与猫咪互动",
            "保持猫砂盆清洁很重要",
            "给猫咪提供足够的活动空间"
    };
    
    private final Random random = new Random();
    
    @Override
    public AiAvatarResponse generateAvatar(Long userId, AiAvatarRequest request) {
        checkUsageLimit(userId);
        
        String style = request.getStyle();
        if (style == null || style.isEmpty()) {
            style = AVATAR_STYLES[random.nextInt(AVATAR_STYLES.length)];
        }
        
        // TODO: 接入真实AI图片生成API
        // 示例：调用DALL-E / Stable Diffusion等图片生成服务
        // String prompt = "Generate a " + style + " style cat avatar";
        // String imageUrl = callImageGenerationApi(prompt);
        String generatedUrl = "https://example.com/ai/avatar/" + System.currentTimeMillis() + "?style=" + style;
        
        log.info("用户 {} 生成猫咪头像, style: {}", userId, style);
        return AiAvatarResponse.builder()
                .url(generatedUrl)
                .style(style)
                .build();
    }
    
    @Override
    public AiQuoteResponse generateQuote(Long userId) {
        checkUsageLimit(userId);
        
        // TODO: 接入真实AI文本生成API
        // 示例：调用ChatGPT / 文心一言等大语言模型
        // String prompt = "生成一句可爱的猫咪语录";
        // String quote = callTextGenerationApi(prompt);
        String quote = QUOTES[random.nextInt(QUOTES.length)];
        
        log.info("用户 {} 生成猫咪语录", userId);
        return AiQuoteResponse.builder()
                .quote(quote)
                .build();
    }
    
    @Override
    public AiAdviceResponse generateAdvice(Long userId, AiAdviceRequest request) {
        checkUsageLimit(userId);
        
        String topic = request.getTopic();
        if (topic == null || topic.isEmpty()) {
            topic = ADVICE_TOPICS[random.nextInt(ADVICE_TOPICS.length)];
        }
        
        // TODO: 接入真实AI文本生成API
        // 示例：调用ChatGPT / 文心一言等大语言模型
        // String prompt = "作为宠物专家，请给出关于" + topic + "的养猫建议";
        // String advice = callTextGenerationApi(prompt);
        String advice = ADVICE_CONTENTS[random.nextInt(ADVICE_CONTENTS.length)];
        
        log.info("用户 {} 生成饲养建议, topic: {}", userId, topic);
        return AiAdviceResponse.builder()
                .topic(topic)
                .advice(advice)
                .build();
    }
    
    @Override
    public int getTodayUsage(Long userId) {
        return pointsService.getTodayAiUsage(userId);
    }
    
    private void checkUsageLimit(Long userId) {
        int usage = pointsService.getTodayAiUsage(userId);
        if (usage >= DAILY_LIMIT) {
            throw BusinessException.badRequest("今日AI使用次数已达上限");
        }
    }
}
