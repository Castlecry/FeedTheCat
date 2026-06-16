package com.example.catguardian.controller;

import com.example.catguardian.dto.request.RealNameAuthRequest;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.RealNameAuthResponse;
import com.example.catguardian.dto.response.UserProfileResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserProfileResponse response = userService.getProfile(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Authentication authentication,
            @RequestBody User user) {
        User currentUser = (User) authentication.getPrincipal();
        UserProfileResponse response = userService.updateProfile(currentUser.getId(), user);
        return ResponseEntity.ok(ApiResponse.success("更新成功", response));
    }
    
    @PostMapping("/auth/realname")
    public ResponseEntity<ApiResponse<RealNameAuthResponse>> realNameAuth(
            Authentication authentication,
            @Valid @RequestBody RealNameAuthRequest request) {
        User user = (User) authentication.getPrincipal();
        RealNameAuthResponse response = userService.realNameAuth(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("实名认证申请提交成功，等待审核", response));
    }
    
    @GetMapping("/auth/realname")
    public ResponseEntity<ApiResponse<RealNameAuthResponse>> getRealNameAuthStatus(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RealNameAuthResponse response = userService.getRealNameAuthStatus(user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
