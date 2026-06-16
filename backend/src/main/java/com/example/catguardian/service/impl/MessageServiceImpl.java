package com.example.catguardian.service.impl;

import com.example.catguardian.entity.Message;
import com.example.catguardian.entity.User;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.MessageRepository;
import com.example.catguardian.repository.UserRepository;
import com.example.catguardian.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        if (senderId.equals(receiverId)) {
            throw BusinessException.badRequest("不能给自己发送消息");
        }
        
        userRepository.findById(receiverId)
                .orElseThrow(() -> BusinessException.notFound("接收用户不存在"));
        
        Message message = Message.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .type("text")
                .readStatus(0)
                .build();
        
        Message saved = messageRepository.save(message);
        log.info("用户 {} 发送消息给用户 {}", senderId, receiverId);
        return saved;
    }
    
    @Override
    public List<Message> getConversation(Long userId, Long otherUserId) {
        return messageRepository.findBySenderIdAndReceiverIdOrderByCreatedAtAsc(userId, otherUserId);
    }
    
    @Override
    public List<User> getRecentContacts(Long userId) {
        List<Long> senderIds = messageRepository.findDistinctSenders(userId);
        return senderIds.stream()
                .map(id -> userRepository.findById(id))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void markAsRead(Long receiverId, Long senderId) {
        messageRepository.markAsRead(receiverId, senderId);
        log.info("用户 {} 将来自 {} 的消息标记为已读", receiverId, senderId);
    }
}
