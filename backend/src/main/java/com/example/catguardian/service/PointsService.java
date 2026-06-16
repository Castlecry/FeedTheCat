package com.example.catguardian.service;

import com.example.catguardian.dto.response.PointsBalanceResponse;
import com.example.catguardian.entity.PointsRecord;

import java.util.List;

public interface PointsService {
    
    PointsBalanceResponse getBalance(Long userId);
    
    List<PointsRecord> getRecords(Long userId);
    
    void addPoints(Long userId, Integer amount, String source, Long relatedId);
    
    void deductPoints(Long userId, Integer amount, String source, Long relatedId);
    
    void checkIn(Long userId);
    
    int getTodayAiUsage(Long userId);
}