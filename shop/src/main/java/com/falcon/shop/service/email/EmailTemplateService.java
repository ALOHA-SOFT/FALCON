package com.falcon.shop.service.email;

import java.util.List;

import com.falcon.shop.domain.email.EmailTemplate;
import com.falcon.shop.service.BaseService;

public interface EmailTemplateService extends BaseService<EmailTemplate> {
    
    /**
     * 템플릿 타입으로 템플릿 조회
     * @param type 템플릿 타입
     * @return 이메일 템플릿
     */
    EmailTemplate getByType(String type);
    
    /**
     * 활성화된 템플릿 목록 조회
     * @return 활성화된 템플릿 목록
     */
    List<EmailTemplate> getActiveTemplates();
    
    /**
     * 템플릿 활성화/비활성화
     * @param no 템플릿 번호
     * @param isActive 활성화 여부
     * @return 수정 성공 여부
     */
    boolean updateActiveStatus(Long no, Boolean isActive);
}
