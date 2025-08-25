package com.falcon.shop.service.file;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.falcon.shop.domain.common.Files;

/**
 * 파일 업로드 전략 인터페이스
 */
public interface FileUploadStrategy {
    
    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param filePath 파일 경로 (S3의 경우 키, 로컬의 경우 디렉토리 경로)
     * @return 업로드된 파일 정보
     * @throws IOException
     */
    Files uploadFile(MultipartFile file, String filePath) throws IOException;
    
    /**
     * 파일 삭제
     * @param filePath 삭제할 파일 경로
     * @return 삭제 성공 여부
     */
    boolean deleteFile(String filePath);
    
    /**
     * 파일 URL 반환
     * @param filePath 파일 경로
     * @return 파일 접근 URL
     */
    String getFileUrl(String filePath);
    
    /**
     * 업로드 타입 반환
     * @return 업로드 타입 (local, s3)
     */
    String getUploadType();
}
