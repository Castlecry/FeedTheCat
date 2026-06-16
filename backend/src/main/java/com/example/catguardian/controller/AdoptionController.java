package com.example.catguardian.controller;

import com.example.catguardian.dto.request.CreateAdoptionRequest;
import com.example.catguardian.dto.request.CreateAdoptionTrackingRequest;
import com.example.catguardian.dto.request.CreateStrayCatRequest;
import com.example.catguardian.dto.request.StrayCatFilterRequest;
import com.example.catguardian.dto.response.AdoptionApplicationResponse;
import com.example.catguardian.dto.response.AdoptionTrackingResponse;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.StrayCatResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.AdoptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 领养控制器
 */
@RestController
@RequestMapping("/api/adopt")
@RequiredArgsConstructor
public class AdoptionController {
    
    private final AdoptionService adoptionService;
    
    @PostMapping("/cats")
    public ResponseEntity<ApiResponse<StrayCatResponse>> publishStrayCat(
            Authentication authentication,
            @Valid @RequestBody CreateStrayCatRequest request) {
        User user = (User) authentication.getPrincipal();
        StrayCatResponse response = adoptionService.publishStrayCat(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("发布成功", response));
    }
    
    @GetMapping("/cats")
    public ResponseEntity<ApiResponse<List<StrayCatResponse>>> getAvailableCats() {
        List<StrayCatResponse> cats = adoptionService.getAvailableCats();
        return ResponseEntity.ok(ApiResponse.success(cats));
    }
    
    @PostMapping("/cats/filter")
    public ResponseEntity<ApiResponse<List<StrayCatResponse>>> filterCats(
            @RequestBody(required = false) StrayCatFilterRequest request) {
        if (request == null) {
            request = new StrayCatFilterRequest();
        }
        List<StrayCatResponse> cats = adoptionService.filterCats(request);
        return ResponseEntity.ok(ApiResponse.success(cats));
    }
    
    @GetMapping("/cats/{id}")
    public ResponseEntity<ApiResponse<StrayCatResponse>> getStrayCatById(@PathVariable Long id) {
        StrayCatResponse response = adoptionService.getStrayCatById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/cats/feeder")
    public ResponseEntity<ApiResponse<List<StrayCatResponse>>> getMyCats(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<StrayCatResponse> cats = adoptionService.getCatsByFeeder(user.getId());
        return ResponseEntity.ok(ApiResponse.success(cats));
    }
    
    @PutMapping("/cats/{id}/review")
    public ResponseEntity<ApiResponse<StrayCatResponse>> reviewStrayCat(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        StrayCatResponse response = adoptionService.reviewStrayCat(id, status);
        return ResponseEntity.ok(ApiResponse.success("审核完成", response));
    }
    
    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<AdoptionApplicationResponse>> applyAdoption(
            Authentication authentication,
            @Valid @RequestBody CreateAdoptionRequest request) {
        User user = (User) authentication.getPrincipal();
        AdoptionApplicationResponse response = adoptionService.applyAdoption(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("申请提交成功", response));
    }
    
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<AdoptionApplicationResponse>>> getMyApplications(
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<AdoptionApplicationResponse> applications = adoptionService.getApplicationsByApplicant(user.getId());
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
    
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<AdoptionApplicationResponse>> getApplicationById(@PathVariable Long id) {
        AdoptionApplicationResponse response = adoptionService.getAdoptionApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/cats/{catId}/applications")
    public ResponseEntity<ApiResponse<List<AdoptionApplicationResponse>>> getApplicationsByCat(
            @PathVariable Long catId) {
        List<AdoptionApplicationResponse> applications = adoptionService.getApplicationsByCat(catId);
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
    
    @PutMapping("/applications/{id}/review")
    public ResponseEntity<ApiResponse<AdoptionApplicationResponse>> reviewApplication(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String note = request.get("note");
        AdoptionApplicationResponse response = adoptionService.reviewApplication(id, note);
        return ResponseEntity.ok(ApiResponse.success("初审完成", response));
    }
    
    @PutMapping("/applications/{id}/feeder-review")
    public ResponseEntity<ApiResponse<AdoptionApplicationResponse>> feederReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Integer status = (Integer) request.get("status");
        String note = (String) request.get("note");
        AdoptionApplicationResponse response = adoptionService.feederReview(id, status, note, user.getId());
        return ResponseEntity.ok(ApiResponse.success("复审完成", response));
    }
    
    @PostMapping("/applications/{id}/tracking")
    public ResponseEntity<ApiResponse<AdoptionTrackingResponse>> addTrackingRecord(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody CreateAdoptionTrackingRequest request) {
        User user = (User) authentication.getPrincipal();
        AdoptionTrackingResponse response = adoptionService.addTrackingRecord(id, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("跟踪记录添加成功", response));
    }
    
    @GetMapping("/applications/{id}/tracking")
    public ResponseEntity<ApiResponse<List<AdoptionTrackingResponse>>> getTrackingRecords(@PathVariable Long id) {
        List<AdoptionTrackingResponse> records = adoptionService.getTrackingRecords(id);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
    
    @GetMapping("/blacklist")
    public ResponseEntity<ApiResponse<List<Object[]>>> getBlacklist() {
        List<Object[]> blacklist = adoptionService.getBlacklist();
        return ResponseEntity.ok(ApiResponse.success(blacklist));
    }
    
    @PostMapping("/blacklist")
    public ResponseEntity<ApiResponse<Void>> addToBlacklist(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {
        Long userId = ((Number) request.get("userId")).longValue();
        String reason = (String) request.get("reason");
        adoptionService.addToBlacklist(userId, reason);
        return ResponseEntity.ok(ApiResponse.success("已加入黑名单"));
    }
    
    @DeleteMapping("/blacklist/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeFromBlacklist(@PathVariable Long userId) {
        adoptionService.removeFromBlacklist(userId);
        return ResponseEntity.ok(ApiResponse.success("已移除黑名单"));
    }
}
