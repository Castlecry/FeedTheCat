package com.example.catguardian.service.impl;

import com.example.catguardian.exception.BusinessException;
import com.example.catguardian.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.upload.base-url:/uploads}")
    private String baseUrl;
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    private static final String[] ALLOWED_IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp"};
    private static final String[] ALLOWED_VIDEO_EXTENSIONS = {".mp4", ".avi", ".mov", ".flv", ".wmv"};
    
    @Override
    public String upload(MultipartFile file) {
        validateFile(file);
        String extension = getFileExtension(file.getOriginalFilename());
        if (isImageExtension(extension)) {
            return uploadImage(file);
        } else if (isVideoExtension(extension)) {
            return uploadVideo(file);
        } else {
            throw new BusinessException("不支持的文件类型");
        }
    }
    
    @Override
    public String uploadImage(MultipartFile file) {
        validateFile(file);
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isImageExtension(extension)) {
            throw new BusinessException("请上传图片文件");
        }
        return saveFile(file, "images");
    }
    
    @Override
    public String uploadVideo(MultipartFile file) {
        validateFile(file);
        String extension = getFileExtension(file.getOriginalFilename());
        if (!isVideoExtension(extension)) {
            throw new BusinessException("请上传视频文件");
        }
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new BusinessException("视频文件不能超过50MB");
        }
        return saveFile(file, "videos");
    }
    
    @Override
    public void delete(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }
        try {
            String relativePath = filePath.replace(baseUrl, "");
            Path path = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(path);
            log.info("文件删除成功: {}", filePath);
        } catch (IOException e) {
            log.error("文件删除失败: {}", filePath, e);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的文件");
        }
        if (file.getSize() > MAX_FILE_SIZE && !isVideoFile(file)) {
            throw new BusinessException("文件大小不能超过10MB");
        }
        if (!StringUtils.hasText(file.getOriginalFilename())) {
            throw new BusinessException("文件名不能为空");
        }
    }
    
    private boolean isVideoFile(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        return isVideoExtension(extension);
    }
    
    private String saveFile(MultipartFile file, String category) {
        try {
            String extension = getFileExtension(file.getOriginalFilename());
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            
            Path uploadPath = Paths.get(uploadDir, category, datePath);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            String url = baseUrl + "/" + category + "/" + datePath + "/" + fileName;
            log.info("文件上传成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败");
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex).toLowerCase();
    }
    
    private boolean isImageExtension(String extension) {
        for (String ext : ALLOWED_IMAGE_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isVideoExtension(String extension) {
        for (String ext : ALLOWED_VIDEO_EXTENSIONS) {
            if (ext.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
