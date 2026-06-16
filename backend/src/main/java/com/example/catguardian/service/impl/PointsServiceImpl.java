package com.example.catguardian.service.impl;

import com.example.catguardian.dto.response.PointsBalanceResponse;
import com.example.catguardian.entity.PointsRecord;
import com.example.catguardian.entity.UserPoints;
import com.example.catguardian.repository.PointsRecordRepository;
import com.example.catguardian.repository.UserPointsRepository;
import com.example.catguardian.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointsServiceImpl implements PointsService {
    
    private final UserPointsRepository userPointsRepository;
    private final PointsRecordRepository pointsRecordRepository;
    
    @Override
    public PointsBalanceResponse getBalance(Long userId) {
        UserPoints userPoints = userPointsRepository.findByUserId(userId)
                .orElse(UserPoints.builder()
                        .userId(userId)
                        .balance(0)
                        .totalEarned(0)
                        .totalSpent(0)
                        .build());
        
        return PointsBalanceResponse.builder()
                .userId(userId)
                .balance(userPoints.getBalance())
                .totalEarned(userPoints.getTotalEarned())
                .totalSpent(userPoints.getTotalSpent())
                .build();
    }
    
    @Override
    public List<PointsRecord> getRecords(Long userId) {
        return pointsRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    @Override
    @Transactional
    public void addPoints(Long userId, Integer amount, String source, Long relatedId) {
        UserPoints userPoints = userPointsRepository.findByUserId(userId)
                .orElseGet(() -> UserPoints.builder()
                        .userId(userId)
                        .balance(0)
                        .totalEarned(0)
                        .totalSpent(0)
                        .build());
        
        int balanceBefore = userPoints.getBalance();
        userPoints.setBalance(balanceBefore + amount);
        userPoints.setTotalEarned(userPoints.getTotalEarned() + amount);
        
        userPointsRepository.save(userPoints);
        
        PointsRecord record = PointsRecord.builder()
                .userId(userId)
                .type(0)
                .source(source)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(userPoints.getBalance())
                .relatedId(relatedId)
                .build();
        
        pointsRecordRepository.save(record);
        log.info("用户 {} 获得积分 {}，来源：{}", userId, amount, source);
    }
    
    @Override
    @Transactional
    public void deductPoints(Long userId, Integer amount, String source, Long relatedId) {
        UserPoints userPoints = userPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("用户积分账户不存在"));
        
        if (userPoints.getBalance() < amount) {
            throw new RuntimeException("积分不足");
        }
        
        int balanceBefore = userPoints.getBalance();
        userPoints.setBalance(balanceBefore - amount);
        userPoints.setTotalSpent(userPoints.getTotalSpent() + amount);
        
        userPointsRepository.save(userPoints);
        
        PointsRecord record = PointsRecord.builder()
                .userId(userId)
                .type(1)
                .source(source)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(userPoints.getBalance())
                .relatedId(relatedId)
                .build();
        
        pointsRecordRepository.save(record);
        log.info("用户 {} 消费积分 {}，来源：{}", userId, amount, source);
    }
    
    @Override
    @Transactional
    public void checkIn(Long userId) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        List<PointsRecord> todayRecords = pointsRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, todayStart, todayEnd)
                .stream()
                .filter(r -> "check_in".equals(r.getSource()))
                .toList();
        
        if (!todayRecords.isEmpty()) {
            throw new com.example.catguardian.exception.BusinessException("今日已签到");
        }
        
        addPoints(userId, 3, "check_in", null);
    }
    
    @Override
    public int getTodayAiUsage(Long userId) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        List<PointsRecord> todayRecords = pointsRecordRepository
                .findByUserIdAndCreatedAtBetween(userId, todayStart, todayEnd)
                .stream()
                .filter(r -> r.getSource().startsWith("ai_"))
                .toList();
        
        return todayRecords.size();
    }
}