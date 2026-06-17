package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 委托订单实体类
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_no", unique = true, nullable = false, length = 32)
    private String orderNo;
    
    @Column(name = "client_id", nullable = false)
    private Long clientId;
    
    @Column(name = "service_id")
    private Long serviceId;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer status = 0;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Column(name = "visit_frequency", nullable = false)
    private Integer visitFrequency;
    
    @Column(name = "feeding_requirements", columnDefinition = "TEXT")
    private String feedingRequirements;
    
    @Column(name = "litter_clean_standard", length = 200)
    private String litterCleanStandard;
    
    @Column(name = "special_care", columnDefinition = "TEXT")
    private String specialCare;
    
    @Column(name = "entry_method", nullable = false)
    private Integer entryMethod;
    
    @Column(name = "key_storage_info", length = 500)
    private String keyStorageInfo;
    
    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;
    
    @Column(nullable = false, length = 500)
    private String address;
    
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "actual_payment", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal actualPayment = BigDecimal.ZERO;
    
    @Column(name = "refund_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal refundAmount = BigDecimal.ZERO;
    
    @Column(name = "commission_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal commissionRate = new BigDecimal("0.1");
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private User client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private User serviceProvider;
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
