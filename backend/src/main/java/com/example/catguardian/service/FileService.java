package com.example.catguardian.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    
    String upload(MultipartFile file);
    
    String uploadImage(MultipartFile file);
    
    String uploadVideo(MultipartFile file);
    
    void delete(String filePath);
}
