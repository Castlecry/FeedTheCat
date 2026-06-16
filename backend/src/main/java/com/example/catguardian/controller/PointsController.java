package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.PointsBalanceResponse;
import com.example.catguardian.entity.PointsRecord;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.PointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointsController {
    
    private final PointsService pointsService;
    
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<PointsBalanceResponse>> getBalance(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        PointsBalanceResponse response = pointsService.getBalance(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<PointsRecord>>> getRecords(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<PointsRecord> records = pointsService.getRecords(user.getId());
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<Void>> checkIn(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        pointsService.checkIn(user.getId());
        return ResponseEntity.ok(ApiResponse.success("签到成功，获得3积分"));
    }
    
    @GetMapping("/ai-usage")
    public ResponseEntity<ApiResponse<Integer>> getAiUsage(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        int usage = pointsService.getTodayAiUsage(user.getId());
        return ResponseEntity.ok(ApiResponse.success(usage));
    }
}