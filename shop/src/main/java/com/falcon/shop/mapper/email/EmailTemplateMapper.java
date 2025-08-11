package com.falcon.shop.mapper.email;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.email.EmailTemplate;

@Mapper
public interface EmailTemplateMapper extends BaseMapper<EmailTemplate> {
    
    /**
     * 템플릿 타입으로 템플릿 조회
     * @param type 템플릿 타입
     * @return 이메일 템플릿
     */
    EmailTemplate selectByType(@Param("type") String type);
    
    /**
     * 활성화된 템플릿 목록 조회
     * @return 활성화된 템플릿 목록
     */
    List<EmailTemplate> selectActiveTemplates();
}
