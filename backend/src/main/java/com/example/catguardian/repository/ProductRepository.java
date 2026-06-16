package com.example.catguardian.repository;

import com.example.catguardian.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryAndStatusOrderByCreatedAtDesc(Integer category, Integer status);
    List<Product> findByStatusOrderByCreatedAtDesc(Integer status);
}