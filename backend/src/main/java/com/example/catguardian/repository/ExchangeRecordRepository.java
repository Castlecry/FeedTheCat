package com.example.catguardian.repository;

import com.example.catguardian.entity.ExchangeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRecordRepository extends JpaRepository<ExchangeRecord, Long> {
    List<ExchangeRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
}
