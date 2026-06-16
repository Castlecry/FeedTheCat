package com.example.catguardian.controller;

import com.example.catguardian.dto.request.AiAdviceRequest;
import com.example.catguardian.dto.request.AiAvatarRequest;
import com.example.catguardian.dto.response.AiAdviceResponse;
import com.example.catguardian.dto.response.AiAvatarResponse;
import com.example.catguardian.dto.response.AiQuoteResponse;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * AI功能控制器
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
    
    private final AiService aiService;
    
    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<AiAvatarResponse>> generateAvatar(
            Authentication authentication,
            @RequestBody AiAvatarRequest request) {
        User user = (User) authentication.getPrincipal();
        AiAvatarResponse response = aiService.generateAvatar(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("头像生成成功", response));
    }
    
    @PostMapping("/quote")
    public ResponseEntity<ApiResponse<AiQuoteResponse>> generateQuote(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        AiQuoteResponse response = aiService.generateQuote(user.getId());
        return ResponseEntity.ok(ApiResponse.success("语录生成成功", response));
    }
    
    @PostMapping("/advice")
    public ResponseEntity<ApiResponse<AiAdviceResponse>> generateAdvice(
            Authentication authentication,
            @RequestBody(required = false) AiAdviceRequest request) {
        User user = (User) authentication.getPrincipal();
        if (request == null) {
            request = new AiAdviceRequest();
        }
        AiAdviceResponse response = aiService.generateAdvice(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("建议生成成功", response));
    }
    
    @GetMapping("/usage")
    public ResponseEntity<ApiResponse<Integer>> getUsage(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        int usage = aiService.getTodayUsage(user.getId());
        return ResponseEntity.ok(ApiResponse.success(usage));
    }
}
