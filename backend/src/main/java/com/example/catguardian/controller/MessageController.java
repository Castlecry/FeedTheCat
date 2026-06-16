package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.entity.Message;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            Authentication authentication,
            @RequestBody java.util.Map<String, Object> request) {
        User user = (User) authentication.getPrincipal();
        Long receiverId = ((Number) request.get("receiverId")).longValue();
        String content = (String) request.get("content");
        Message message = messageService.sendMessage(user.getId(), receiverId, content);
        return ResponseEntity.ok(ApiResponse.success("消息发送成功", message));
    }
    
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<ApiResponse<List<Message>>> getConversation(
            @PathVariable Long otherUserId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Message> messages = messageService.getConversation(user.getId(), otherUserId);
        messageService.markAsRead(user.getId(), otherUserId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<List<User>>> getRecentContacts(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<User> contacts = messageService.getRecentContacts(user.getId());
        return ResponseEntity.ok(ApiResponse.success(contacts));
    }
    
    @PostMapping("/read/{senderId}")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long senderId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        messageService.markAsRead(user.getId(), senderId);
        return ResponseEntity.ok(ApiResponse.success("消息已标记为已读"));
    }
}
