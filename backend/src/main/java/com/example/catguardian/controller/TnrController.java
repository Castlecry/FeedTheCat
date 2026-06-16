package com.example.catguardian.controller;

import com.example.catguardian.dto.request.CreateTnrApplicationRequest;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.TnrApplicationResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.TnrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tnr")
@RequiredArgsConstructor
public class TnrController {
    
    private final TnrService tnrService;
    
    @PostMapping("/applications")
    public ResponseEntity<ApiResponse<TnrApplicationResponse>> createApplication(
            Authentication authentication,
            @Valid @RequestBody CreateTnrApplicationRequest request) {
        User user = (User) authentication.getPrincipal();
        TnrApplicationResponse response = tnrService.createApplication(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("TNR申请创建成功", response));
    }
    
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<TnrApplicationResponse>>> getApplications() {
        List<TnrApplicationResponse> applications = tnrService.getApplications();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
    
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<TnrApplicationResponse>> getApplication(@PathVariable Long id) {
        TnrApplicationResponse response = tnrService.getApplication(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveApplication(@PathVariable Long id) {
        tnrService.approveApplication(id);
        return ResponseEntity.ok(ApiResponse.success("TNR申请已通过"));
    }
    
    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectApplication(
            @PathVariable Long id,
            @RequestBody(required = false) java.util.Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        if (reason != null && !reason.isEmpty()) {
            tnrService.rejectApplication(id, reason);
        } else {
            tnrService.rejectApplication(id);
        }
        return ResponseEntity.ok(ApiResponse.success("TNR申请已拒绝"));
    }
    
    @PostMapping("/applications/{id}/photos")
    public ResponseEntity<ApiResponse<TnrApplicationResponse>> uploadOperationPhotos(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> request) {
        String photos = request.get("photos");
        TnrApplicationResponse response = tnrService.uploadOperationPhotos(id, photos);
        return ResponseEntity.ok(ApiResponse.success("手术照片上传成功", response));
    }
}