package com.example.catguardian.controller;

import com.example.catguardian.dto.request.CreateCatProfileRequest;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.CatProfileResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.CatProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/cats")
@RequiredArgsConstructor
public class CatProfileController {
    
    private final CatProfileService catProfileService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CatProfileResponse>> createCatProfile(
            Authentication authentication,
            @Valid @RequestBody CreateCatProfileRequest request) {
        User user = (User) authentication.getPrincipal();
        CatProfileResponse response = catProfileService.createProfile(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("猫咪档案创建成功", response));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CatProfileResponse>>> getMyCats(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<CatProfileResponse> cats = catProfileService.getProfilesByUser(user.getId());
        return ResponseEntity.ok(ApiResponse.success(cats));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CatProfileResponse>> getCatProfile(@PathVariable Long id) {
        CatProfileResponse response = catProfileService.getProfile(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CatProfileResponse>> updateCatProfile(
            @PathVariable Long id,
            @Valid @RequestBody CreateCatProfileRequest request) {
        CatProfileResponse response = catProfileService.updateProfile(id, request);
        return ResponseEntity.ok(ApiResponse.success("猫咪档案更新成功", response));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCatProfile(@PathVariable Long id) {
        catProfileService.deleteProfile(id);
        return ResponseEntity.ok(ApiResponse.success("猫咪档案删除成功"));
    }
}