package com.falcon.shop.service.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.falcon.shop.domain.common.Files;

import lombok.extern.slf4j.Slf4j;

/**
 * 로컬 파일 시스템 업로드 전략
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.upload.type", havingValue = "local", matchIfMissing = true)
public class LocalFileUploadStrategy implements FileUploadStrategy {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public Files uploadFile(MultipartFile file, String subDir) throws IOException {
        log.info("🔄 Local file upload started: {}", file.getOriginalFilename());
        
        // 날짜별 디렉토리 생성
        String dateDir = LocalDate.now().toString().replace("-", "/");
        String fullPath = uploadPath + "/" + subDir + "/" + dateDir;
        
        // 디렉토리 생성
        Path directoryPath = Paths.get(fullPath);
        if (!java.nio.file.Files.exists(directoryPath)) {
            java.nio.file.Files.createDirectories(directoryPath);
        }
        
        // 파일명 생성 (UUID + 원본 확장자)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        
        // 파일 저장
        Path filePath = directoryPath.resolve(fileName);
        file.transferTo(filePath.toFile());
        
        // 상대 경로 생성 (웹에서 접근 가능한 경로)
        String relativePath = "/" + subDir + "/" + dateDir + "/" + fileName;
        
        log.info("✅ Local file upload completed: {}", relativePath);
        
        // Files 객체 생성
        Files uploadedFile = new Files();
        uploadedFile.setFullName(originalFilename);  // 원본 파일명
        uploadedFile.setFileName(fileName);          // 저장된 파일명
        uploadedFile.setFileSize(file.getSize());    // 파일 크기
        uploadedFile.setSubPath(relativePath);       // 파일 경로
        
        return uploadedFile;
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            String fullPath = uploadPath + filePath;
            File file = new File(fullPath);
            if (file.exists()) {
                boolean deleted = file.delete();
                log.info("🗑️ Local file delete: {} - {}", filePath, deleted ? "SUCCESS" : "FAILED");
                return deleted;
            }
            return false;
        } catch (Exception e) {
            log.error("❌ Local file delete error: {}", filePath, e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        // 로컬 파일의 경우 정적 리소스 경로로 반환
        return "/upload" + filePath;
    }

    @Override
    public String getUploadType() {
        return "local";
    }
}
