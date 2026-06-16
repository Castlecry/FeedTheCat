package com.example.catguardian.repository;

import com.example.catguardian.entity.OrderEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderEvaluationRepository extends JpaRepository<OrderEvaluation, Long> {
    Optional<OrderEvaluation> findByOrderId(Long orderId);
    boolean existsByOrderId(Long orderId);
}