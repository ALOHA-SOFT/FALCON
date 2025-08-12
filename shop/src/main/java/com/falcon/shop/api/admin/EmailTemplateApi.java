package com.falcon.shop.api.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.falcon.shop.domain.email.EmailTemplate;
import com.falcon.shop.service.email.EmailService;
import com.falcon.shop.service.email.EmailTemplateService;
import com.falcon.shop.service.email.RealEmailService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/email-template")
public class EmailTemplateApi {
  
    @Autowired private EmailTemplateService emailTemplateService;
    @Autowired private EmailService emailService;
    
    @GetMapping()
    public ResponseEntity<?> getAll(
        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
        @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        try {
            return new ResponseEntity<>(emailTemplateService.page(page, size), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email template list", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") String id) {
        try {
            return new ResponseEntity<>(emailTemplateService.selectById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email template by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getByType(@PathVariable("type") String type) {
        try {
            return new ResponseEntity<>(emailTemplateService.getByType(type), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting email template by type: {}", type, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActiveTemplates() {
        try {
            return new ResponseEntity<>(emailTemplateService.getActiveTemplates(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error getting active email templates", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PostMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> createForm(EmailTemplate emailTemplate) {
        log.info("## FORM ##");
        log.info("emailTemplate={}", emailTemplate);
        try {
            boolean result = emailTemplateService.insert(emailTemplate);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error creating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> create(@RequestBody EmailTemplate emailTemplate) {
        log.info("## JSON ##");
        log.info("emailTemplate={}", emailTemplate);
        try {
            boolean result = emailTemplateService.insert(emailTemplate);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error creating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<?> updateForm(EmailTemplate emailTemplate) {
        try {
            boolean result = emailTemplateService.updateById(emailTemplate);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error updating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @PutMapping(path = "", consumes = "application/json")
    public ResponseEntity<?> update(@RequestBody EmailTemplate emailTemplate) {
        try {
            boolean result = emailTemplateService.updateById(emailTemplate);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("Error updating email template", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") String id) {
        try {
            boolean result = emailTemplateService.deleteById(id);
            if (result) {
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("FAIL", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error deleting email template by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 템플릿 활성화/비활성화 토글
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable("id") String id) {
        try {
            EmailTemplate template = emailTemplateService.selectById(id);
            if (template == null) {
                return new ResponseEntity<>("FAIL", HttpStatus.NOT_FOUND);
            }
            
            template.setIsActive(!template.getIsActive());
            emailTemplateService.updateById(template);
            
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error toggling template status by id: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/test")
    public ResponseEntity<?> sendTestEmail(
        @PathVariable("id") String id,
        @RequestBody Map<String, String> payload
    ) {
        try {
            String email = payload.get("email");
            String name = payload.get("name");
            String variablesJson = payload.get("variables");
            
            log.info("테스트 이메일 발송 요청 - id: {}, email: {}, name: {}", id, email, name);
            
            EmailTemplate template = emailTemplateService.selectById(id);
            if (template == null) {
                log.error("템플릿을 찾을 수 없습니다: {}", id);
                return new ResponseEntity<>("TEMPLATE_NOT_FOUND", HttpStatus.NOT_FOUND);
            }
            
            // 변수 치환을 위한 기본값 설정
            Map<String, Object> variables = new java.util.HashMap<>();
            variables.put("customerName", name != null ? name : "테스트 사용자");
            variables.put("companyName", "Falcon Cartons");
            variables.put("orderCode", "TEST-" + System.currentTimeMillis());
            variables.put("orderDate", java.time.LocalDateTime.now().toString());
            variables.put("paymentMethod", "테스트 결제");
            variables.put("username", name != null ? name : "testuser");
            variables.put("tempPassword", "test123456");
            variables.put("email", email);
            
            // variablesJson이 있다면 파싱해서 덮어쓰기
            if (variablesJson != null && !variablesJson.trim().isEmpty()) {
                try {
                    // 간단한 JSON 파싱 (실제로는 ObjectMapper 사용 권장)
                    log.info("추가 변수: {}", variablesJson);
                } catch (Exception e) {
                    log.warn("변수 JSON 파싱 실패: {}", variablesJson, e);
                }
            }
            
            // 템플릿 변수 치환
            String subject = replaceVariables(template.getSubject(), variables);
            String htmlContent = replaceVariables(template.getContent(), variables);
            
            log.info("치환된 제목: {}", subject);
            log.info("치환된 내용 길이: {} characters", htmlContent.length());
            
            // 이메일 발송 (OrderApi와 동일한 방식)
            boolean result = emailService.sendHtmlEmail(email, subject, htmlContent);
            
            if (result) {
                log.info("테스트 이메일 발송 성공: {}", email);
                return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
            } else {
                log.error("테스트 이메일 발송 실패: {}", email);
                return new ResponseEntity<>("EMAIL_SEND_FAILED", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("테스트 이메일 발송 중 오류 발생 - template id: {}, error: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 템플릿 변수 치환
     */
    private String replaceVariables(String template, Map<String, Object> variables) {
        if (template == null) return "";
        
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        return result;
    }
}
