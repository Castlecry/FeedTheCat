package com.example.catguardian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_provider_credentials")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProviderCredential {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    @Column(name = "id_card_front", nullable = false, length = 255)
    private String idCardFront;
    
    @Column(name = "id_card_back", nullable = false, length = 255)
    private String idCardBack;
    
    @Column(name = "criminal_record", length = 255)
    private String criminalRecord;
    
    @Column(name = "training_certificate", length = 255)
    private String trainingCertificate;
    
    @Column(name = "has_signed_agreement")
    private Integer hasSignedAgreement;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = 0;
        }
        if (hasSignedAgreement == null) {
            hasSignedAgreement = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}