package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    
    private final FollowService followService;
    
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long userId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        followService.follow(user.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("关注成功"));
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        followService.unfollow(user.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("取消关注成功"));
    }
    
    @GetMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(
            @PathVariable Long userId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean status = followService.isFollowing(user.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success(status));
    }
    
    @GetMapping("/following")
    public ResponseEntity<ApiResponse<List<User>>> getFollowing(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<User> following = followService.getFollowing(user.getId());
        return ResponseEntity.ok(ApiResponse.success(following));
    }
    
    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<List<User>>> getFollowers(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<User> followers = followService.getFollowers(user.getId());
        return ResponseEntity.ok(ApiResponse.success(followers));
    }
}
