package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.HospitalDiscountResponse;
import com.example.catguardian.dto.response.HospitalResponse;
import com.example.catguardian.entity.Hospital;
import com.example.catguardian.entity.HospitalDiscount;
import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.repository.HospitalDiscountRepository;
import com.example.catguardian.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    
    private final HospitalRepository hospitalRepository;
    private final HospitalDiscountRepository hospitalDiscountRepository;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<HospitalResponse>>> getHospitals() {
        List<HospitalResponse> hospitals = hospitalRepository.findByStatusOrderByCreatedAtDesc(1).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(hospitals));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalResponse>> getHospital(@PathVariable Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("医院不存在"));
        return ResponseEntity.ok(ApiResponse.success(convertToResponse(hospital)));
    }
    
    @GetMapping("/{id}/discounts")
    public ResponseEntity<ApiResponse<List<HospitalDiscountResponse>>> getDiscounts(@PathVariable Long id) {
        List<HospitalDiscountResponse> discounts = hospitalDiscountRepository
                .findByHospitalIdAndStatusOrderByStartDateAsc(id, 1)
                .stream()
                .map(this::convertToDiscountResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(discounts));
    }
    
    private HospitalResponse convertToResponse(Hospital hospital) {
        return HospitalResponse.builder()
                .id(hospital.getId())
                .name(hospital.getName())
                .address(hospital.getAddress())
                .phone(hospital.getPhone())
                .businessHours(hospital.getBusinessHours())
                .description(hospital.getDescription())
                .photos(hospital.getPhotos())
                .status(hospital.getStatus())
                .createdAt(hospital.getCreatedAt())
                .build();
    }
    
    private HospitalDiscountResponse convertToDiscountResponse(HospitalDiscount discount) {
        return HospitalDiscountResponse.builder()
                .id(discount.getId())
                .hospitalId(discount.getHospitalId())
                .title(discount.getTitle())
                .description(discount.getDescription())
                .discountType(discount.getDiscountType())
                .discountValue(discount.getDiscountValue())
                .minAmount(discount.getMinAmount())
                .startDate(discount.getStartDate())
                .endDate(discount.getEndDate())
                .status(discount.getStatus())
                .createdAt(discount.getCreatedAt())
                .build();
    }
}