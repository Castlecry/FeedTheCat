package com.example.catguardian.repository;

import com.example.catguardian.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByOrderId(Long orderId);
    List<ServiceRecord> findByOrderIdOrderByServiceTimeDesc(Long orderId);
}