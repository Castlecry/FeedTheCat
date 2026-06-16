package com.example.catguardian.controller;

import com.example.catguardian.dto.request.CreateCommentRequest;
import com.example.catguardian.dto.request.CreateCommunityPostRequest;
import com.example.catguardian.dto.request.CreateCommunityTaskRequest;
import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.dto.response.CommentResponse;
import com.example.catguardian.dto.response.CommunityPostResponse;
import com.example.catguardian.dto.response.CommunityTaskResponse;
import com.example.catguardian.entity.User;
import com.example.catguardian.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;
    
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<CommunityPostResponse>>> getPosts(
            @RequestParam(required = false) Integer type,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<CommunityPostResponse> posts = communityService.getPosts(type, user.getId());
        return ResponseEntity.ok(ApiResponse.success(posts));
    }
    
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> getPost(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        CommunityPostResponse response = communityService.getPost(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> createPost(
            Authentication authentication,
            @Valid @RequestBody CreateCommunityPostRequest request) {
        User user = (User) authentication.getPrincipal();
        CommunityPostResponse response = communityService.createPost(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("帖子发布成功", response));
    }
    
    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<CommunityPostResponse>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CreateCommunityPostRequest request) {
        CommunityPostResponse response = communityService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("帖子更新成功", response));
    }
    
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("帖子删除成功"));
    }
    
    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long id) {
        List<CommentResponse> comments = communityService.getComments(id);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
    
    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody CreateCommentRequest request) {
        User user = (User) authentication.getPrincipal();
        CommentResponse response = communityService.createComment(id, user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("评论成功", response));
    }
    
    @PostMapping("/posts/{id}/like")
    public ResponseEntity<ApiResponse<Void>> likePost(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        communityService.likePost(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("点赞成功"));
    }
    
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<CommunityTaskResponse>>> getTasks() {
        List<CommunityTaskResponse> tasks = communityService.getTasks();
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }
    
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<CommunityTaskResponse>> createTask(
            Authentication authentication,
            @Valid @RequestBody CreateCommunityTaskRequest request) {
        User user = (User) authentication.getPrincipal();
        CommunityTaskResponse response = communityService.createTask(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("任务发布成功", response));
    }
    
    @PostMapping("/tasks/{id}/claim")
    public ResponseEntity<ApiResponse<Void>> claimTask(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        communityService.claimTask(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("任务认领成功"));
    }
}