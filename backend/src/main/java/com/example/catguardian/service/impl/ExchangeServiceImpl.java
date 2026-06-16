package com.example.catguardian.service.impl;

import com.example.catguardian.entity.ExchangeItem;
import com.example.catguardian.entity.ExchangeRecord;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.ExchangeItemRepository;
import com.example.catguardian.repository.ExchangeRecordRepository;
import com.example.catguardian.service.ExchangeService;
import com.example.catguardian.service.PointsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeServiceImpl implements ExchangeService {
    
    private final ExchangeItemRepository exchangeItemRepository;
    private final ExchangeRecordRepository exchangeRecordRepository;
    private final PointsService pointsService;
    
    @Override
    public List<ExchangeItem> getExchangeItems() {
        return exchangeItemRepository.findByStatus(1);
    }
    
    @Override
    public List<ExchangeItem> getExchangeItemsByType(String type) {
        return exchangeItemRepository.findByTypeAndStatus(type, 1);
    }
    
    @Override
    public ExchangeItem getExchangeItem(Long id) {
        return exchangeItemRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("兑换商品不存在"));
    }
    
    @Override
    @Transactional
    public ExchangeRecord exchange(Long userId, Long itemId) {
        ExchangeItem item = exchangeItemRepository.findById(itemId)
                .orElseThrow(() -> BusinessException.notFound("兑换商品不存在"));
        
        if (item.getStatus() != 1) {
            throw BusinessException.badRequest("商品已下架");
        }
        
        if (item.getStock() > 0 && item.getStock() <= 0) {
            throw BusinessException.badRequest("商品库存不足");
        }
        
        pointsService.deductPoints(userId, item.getPoints(), "exchange", itemId);
        
        if (item.getStock() > 0) {
            item.setStock(item.getStock() - 1);
            exchangeItemRepository.save(item);
        }
        
        String orderNo = "EX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) 
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        ExchangeRecord record = ExchangeRecord.builder()
                .userId(userId)
                .itemId(itemId)
                .itemName(item.getName())
                .points(item.getPoints())
                .status(0)
                .orderNo(orderNo)
                .build();
        
        ExchangeRecord saved = exchangeRecordRepository.save(record);
        log.info("用户 {} 兑换商品 {} 成功，消耗积分 {}", userId, itemId, item.getPoints());
        return saved;
    }
    
    @Override
    public List<ExchangeRecord> getExchangeRecords(Long userId) {
        return exchangeRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
