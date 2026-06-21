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
    private static final String[] MOCK_ADVICES = {
            "建议每天定时喂食，保持饮食规律，避免频繁更换猫粮品牌。如果猫咪食欲不振，可以尝试将猫粮稍微加热，增加食物的香味来吸引猫咪进食。",
            "定期带猫咪去医院做体检，建议每年至少一次全面检查。注意观察猫咪的精神状态、食欲和排泄情况，发现异常及时就医。",
            "猫咪抓沙发是天性，建议提供猫抓板或猫爬架作为替代。可以在沙发上贴双面胶或使用防抓喷雾，同时用逗猫棒引导猫咪使用猫抓板。",
            "保持猫砂盆清洁很重要，建议每天至少清理一次，每周彻底更换一次猫砂。如果猫咪突然不在猫砂盆排泄，可能是健康问题或压力导致，需要关注。",
            "给猫咪提供足够的活动空间和玩具，每天至少互动玩耍15-20分钟。窗台上的观景位可以让猫咪观察外面的世界，有助于缓解无聊和焦虑。"
    };
    
    private final Random random = new Random();
    
    @Override
    public AiAvatarResponse generateAvatar(Long userId, AiAvatarRequest request) {
        checkUsageLimit(userId);
        
        String prompt = request.getPrompt();
        String style = request.getStyle();
        if (style == null || style.isEmpty()) {
            style = "cartoon";
        }
        
        // TODO: 接入真实AI图片生成API
        // 示例：调用DALL-E / Stable Diffusion等图片生成服务
        // String fullPrompt = "Generate a " + style + " style cat avatar: " + prompt;
        // String imageUrl = callImageGenerationApi(fullPrompt);
        String generatedUrl = "https://example.com/ai/avatar/" + System.currentTimeMillis() + "?style=" + style + "&prompt=" + prompt.hashCode();
        
        log.info("用户 {} 生成猫咪头像, prompt: {}, style: {}", userId, prompt, style);
        return AiAvatarResponse.builder()
                .prompt(prompt)
                .style(style)
                .url(generatedUrl)
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
        
        String question = request.getQuestion();
        String catBreed = request.getCatBreed();
        String catAge = request.getCatAge();
        
        // TODO: 接入真实AI文本生成API
        // 示例：调用ChatGPT / 文心一言等大语言模型
        // StringBuilder promptBuilder = new StringBuilder();
        // promptBuilder.append("作为宠物专家，请回答以下问题：").append(question);
        // if (catBreed != null) promptBuilder.append("。猫咪品种：").append(catBreed);
        // if (catAge != null) promptBuilder.append("。猫咪年龄：").append(catAge);
        // String advice = callTextGenerationApi(promptBuilder.toString());
        String advice = MOCK_ADVICES[random.nextInt(MOCK_ADVICES.length)];
        
        log.info("用户 {} 生成饲养建议, question: {}, catBreed: {}, catAge: {}", userId, question, catBreed, catAge);
        return AiAdviceResponse.builder()
                .question(question)
                .catBreed(catBreed)
                .catAge(catAge)
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
