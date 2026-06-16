package com.example.catguardian.repository;

import com.example.catguardian.entity.HospitalDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalDiscountRepository extends JpaRepository<HospitalDiscount, Long> {
    List<HospitalDiscount> findByHospitalIdAndStatusOrderByStartDateAsc(Long hospitalId, Integer status);
}