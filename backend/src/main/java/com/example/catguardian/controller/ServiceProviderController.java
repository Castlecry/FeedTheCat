package com.example.catguardian.controller;

import com.example.catguardian.dto.request.ServiceProviderApplyRequest;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.entity.ServiceProviderCredential;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.ServiceProviderCredentialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-provider")
@RequiredArgsConstructor
public class ServiceProviderController {
    
    private final ServiceProviderCredentialService credentialService;
    
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<ServiceProviderCredential>> apply(
            Authentication authentication,
            @Valid @RequestBody ServiceProviderApplyRequest request) {
        User user = (User) authentication.getPrincipal();
        ServiceProviderCredential response = credentialService.apply(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("资质申请提交成功，等待审核", response));
    }
    
    @GetMapping("/credentials")
    public ResponseEntity<ApiResponse<ServiceProviderCredential>> getCredentials(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        ServiceProviderCredential response = credentialService.getByUserId(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ServiceProviderCredential>>> getPendingApplications() {
        List<ServiceProviderCredential> applications = credentialService.getPendingApplications();
        return ResponseEntity.ok(ApiResponse.success(applications));
    }
    
    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<ApiResponse<ServiceProviderCredential>> approve(@PathVariable Long id) {
        ServiceProviderCredential response = credentialService.approve(id);
        return ResponseEntity.ok(ApiResponse.success("审核通过", response));
    }
    
    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<ApiResponse<ServiceProviderCredential>> reject(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.getOrDefault("reason", "审核未通过");
        ServiceProviderCredential response = credentialService.reject(id, reason);
        return ResponseEntity.ok(ApiResponse.success("已拒绝", response));
    }
}