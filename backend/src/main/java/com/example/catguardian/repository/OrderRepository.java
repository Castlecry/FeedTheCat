package com.example.catguardian.repository;

import com.example.catguardian.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNo(String orderNo);
    
    List<Order> findByClientId(Long clientId);
    
    List<Order> findByServiceId(Long serviceId);
    
    List<Order> findByStatus(Integer status);
    
    List<Order> findByClientIdAndStatus(Long clientId, Integer status);
    
    List<Order> findByServiceIdAndStatus(Long serviceId, Integer status);
    
    Page<Order> findByStatus(Integer status, Pageable pageable);
    
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    boolean existsByOrderNo(String orderNo);
}
