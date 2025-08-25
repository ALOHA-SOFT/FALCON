package com.falcon.shop.service.file;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.falcon.shop.domain.common.Files;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 업로드 서비스
 * Strategy 패턴을 사용하여 로컬/S3 업로드를 분기 처리
 */
@Slf4j
@Service
public class FileUploadService {

    private final FileUploadStrategy fileUploadStrategy;

    public FileUploadService(FileUploadStrategy fileUploadStrategy) {
        this.fileUploadStrategy = fileUploadStrategy;
        log.info("🚀 FileUploadService initialized with strategy: {}", fileUploadStrategy.getUploadType());
    }

    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param subDir 하위 디렉토리 (예: "CHEditor", "products", "users")
     * @return 업로드된 파일 정보
     * @throws IOException
     */
    public Files uploadFile(MultipartFile file, String subDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }
        
        log.info("📤 File upload request - Strategy: {}, File: {}, SubDir: {}", 
                fileUploadStrategy.getUploadType(), file.getOriginalFilename(), subDir);
        
        return fileUploadStrategy.uploadFile(file, subDir);
    }

    /**
     * 파일 삭제
     * @param filePath 삭제할 파일 경로
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String filePath) {
        log.info("🗑️ File delete request - Strategy: {}, Path: {}", 
                fileUploadStrategy.getUploadType(), filePath);
        
        return fileUploadStrategy.deleteFile(filePath);
    }

    /**
     * 파일 URL 반환
     * @param filePath 파일 경로
     * @return 파일 접근 URL
     */
    public String getFileUrl(String filePath) {
        return fileUploadStrategy.getFileUrl(filePath);
    }

    /**
     * 현재 사용 중인 업로드 전략 타입
     * @return 업로드 타입 (local, s3)
     */
    public String getUploadType() {
        return fileUploadStrategy.getUploadType();
    }
}
