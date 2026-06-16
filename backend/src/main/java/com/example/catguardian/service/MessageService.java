package com.example.catguardian.service;

import com.example.catguardian.entity.Message;
import com.example.catguardian.entity.User;

import java.util.List;

public interface MessageService {
    
    Message sendMessage(Long senderId, Long receiverId, String content);
    
    List<Message> getConversation(Long userId, Long otherUserId);
    
    List<User> getRecentContacts(Long userId);
    
    void markAsRead(Long receiverId, Long senderId);
}
