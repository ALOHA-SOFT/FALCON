package com.falcon.shop.service.email;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.falcon.shop.domain.email.EmailTemplate;
import com.falcon.shop.mapper.email.EmailTemplateMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailTemplateServiceImpl extends BaseServiceImpl<EmailTemplate, EmailTemplateMapper> implements EmailTemplateService {
    
    @Override
    public EmailTemplate getByType(String type) {
        QueryWrapper<EmailTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        queryWrapper.eq("is_active", true);
        return getOne(queryWrapper);
    }
    
    @Override
    public List<EmailTemplate> getActiveTemplates() {
        QueryWrapper<EmailTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_active", true);
        queryWrapper.orderByAsc("type");
        return list(queryWrapper);
    }
    
    @Override
    public boolean updateActiveStatus(Long no, Boolean isActive) {
        try {
            EmailTemplate template = getById(no);
            if (template == null) {
                log.error("템플릿을 찾을 수 없습니다: {}", no);
                return false;
            }
            
            template.setIsActive(isActive);
            return updateById(template);
            
        } catch (Exception e) {
            log.error("템플릿 상태 업데이트 실패: " + e.getMessage(), e);
            return false;
        }
    }
}
