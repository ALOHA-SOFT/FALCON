package com.falcon.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path:#{null}}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 로컬 파일 업로드인 경우만 정적 리소스 매핑 추가
        if (uploadPath != null && !uploadPath.isEmpty()) {
            // /upload/** 요청을 로컬 파일 시스템의 업로드 경로로 매핑
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:" + uploadPath + "/");
            
            // CHEditor 전용 경로 매핑
            registry.addResourceHandler("/CHEditor/attach/**")
                    .addResourceLocations("file:" + uploadPath + "/CHEditor/");
        }
    }
}
