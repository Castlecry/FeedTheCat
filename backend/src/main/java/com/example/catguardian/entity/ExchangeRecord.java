package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "item_id", nullable = false)
    private Long itemId;
    
    @Column(name = "item_name", length = 100)
    private String itemName;
    
    @Column(name = "points", nullable = false)
    private Integer points;
    
    @Column(name = "status")
    @Builder.Default
    private Integer status = 0;
    
    @Column(name = "order_no", length = 50)
    private String orderNo;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
