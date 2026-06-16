package com.example.catguardian.repository;

import com.example.catguardian.entity.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 商城订单数据访问接口
 */
@Repository
public interface MallOrderRepository extends JpaRepository<MallOrder, Long> {
    
    Optional<MallOrder> findByOrderNo(String orderNo);
    
    List<MallOrder> findByUserId(Long userId);
    
    List<MallOrder> findByStatus(Integer status);
    
    List<MallOrder> findByUserIdAndStatus(Long userId, Integer status);
    
    boolean existsByOrderNo(String orderNo);
}
