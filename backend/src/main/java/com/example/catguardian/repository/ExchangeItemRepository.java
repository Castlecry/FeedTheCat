package com.example.catguardian.repository;

import com.example.catguardian.entity.ExchangeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeItemRepository extends JpaRepository<ExchangeItem, Long> {
    List<ExchangeItem> findByStatus(Integer status);
    List<ExchangeItem> findByTypeAndStatus(String type, Integer status);
}
