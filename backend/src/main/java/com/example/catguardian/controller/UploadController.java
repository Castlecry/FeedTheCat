package com.example.catguardian.controller;

import com.example.catguardian.dto.response.ApiResponse;
import com.example.catguardian.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {
    
    private final FileService fileService;
    
    @PostMapping("/file")
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return ResponseEntity.ok(ApiResponse.success("上传成功", url));
    }
    
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadImage(file);
        return ResponseEntity.ok(ApiResponse.success("图片上传成功", url));
    }
    
    @PostMapping("/video")
    public ResponseEntity<ApiResponse<String>> uploadVideo(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadVideo(file);
        return ResponseEntity.ok(ApiResponse.success("视频上传成功", url));
    }
    
    @DeleteMapping("/file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam("path") String path) {
        fileService.delete(path);
        return ResponseEntity.ok(ApiResponse.success("文件删除成功"));
    }
}
