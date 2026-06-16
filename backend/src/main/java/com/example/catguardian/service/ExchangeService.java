package com.example.catguardian.service;

import com.example.catguardian.entity.ExchangeItem;
import com.example.catguardian.entity.ExchangeRecord;

import java.util.List;

public interface ExchangeService {
    
    List<ExchangeItem> getExchangeItems();
    
    List<ExchangeItem> getExchangeItemsByType(String type);
    
    ExchangeItem getExchangeItem(Long id);
    
    ExchangeRecord exchange(Long userId, Long itemId);
    
    List<ExchangeRecord> getExchangeRecords(Long userId);
}
