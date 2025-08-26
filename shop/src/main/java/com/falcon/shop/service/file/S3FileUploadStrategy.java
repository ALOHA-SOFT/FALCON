package com.falcon.shop.service.file;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.falcon.shop.domain.common.Files;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * AWS S3 파일 업로드 전략
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.upload.type", havingValue = "s3")
public class S3FileUploadStrategy implements FileUploadStrategy {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.url:#{null}}")
    private String s3BaseUrl;

    @Autowired private S3Client s3Client;

    @Override
    public Files uploadFile(MultipartFile file, String subDir) throws IOException {
        log.info("🔄 S3 file upload started: {}", file.getOriginalFilename());
        
        // S3 키 생성 (날짜별 디렉토리 구조)
        String dateDir = LocalDate.now().toString().replace("-", "/");
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        String s3Key = subDir + "/" + dateDir + "/" + fileName;
        
        try {
            // S3 업로드 구현 (ACL 제거 - 버킷 정책으로 공개 접근 처리)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    // .acl(ObjectCannedACL.PUBLIC_READ) 제거 - 버킷에서 ACL 비활성화됨
                    .build();
            
            s3Client.putObject(putObjectRequest, 
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            log.info("✅ S3 file upload completed: {}", s3Key);
            
            // Files 객체 생성
            Files uploadedFile = new Files();
            uploadedFile.setFullName(originalFilename);
            uploadedFile.setFileName(fileName);
            uploadedFile.setFileSize(file.getSize());
            uploadedFile.setSubPath(s3Key);
            
            return uploadedFile;
            
        } catch (Exception e) {
            log.error("❌ S3 file upload error: {}", originalFilename, e);
            throw new IOException("S3 upload failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String s3Key) {
        try {
            // S3 삭제 구현
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            
            log.warn("⚠️ S3 delete simulated (AWS SDK not activated): {}", s3Key);
            return true;
            
        } catch (Exception e) {
            log.error("❌ S3 file delete error: {}", s3Key, e);
            return false;
        }
    }

    @Override
    public String getFileUrl(String s3Key) {
        // S3 public URL 반환
        if (s3BaseUrl != null) {
            return s3BaseUrl + "/" + s3Key;
        }
        // 기본 S3 URL 패턴
        return "https://" + bucketName + ".s3.eu-west-2.amazonaws.com/" + s3Key;
    }

    @Override
    public String getUploadType() {
        return "s3";
    }
}
