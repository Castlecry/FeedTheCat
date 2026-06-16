package com.example.catguardian.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单编号生成器
 */
@Component
public class OrderNoGenerator {
    
    private static final String PREFIX = "MO";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();
    
    /**
     * 生成订单编号
     */
    public String generateOrderNo() {
        String datePart = LocalDateTime.now().format(FORMATTER);
        int randomPart = RANDOM.nextInt(10000);
        return PREFIX + datePart + String.format("%04d", randomPart);
    }
    
    /**
     * 生成商城订单编号
     */
    public String generateMallOrderNo() {
        String datePart = LocalDateTime.now().format(FORMATTER);
        int randomPart = RANDOM.nextInt(10000);
        return "MC" + datePart + String.format("%04d", randomPart);
    }
}
