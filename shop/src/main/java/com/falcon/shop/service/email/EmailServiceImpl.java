package com.falcon.shop.service.email;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.falcon.shop.domain.email.Email;
import com.falcon.shop.domain.email.EmailTemplate;
import com.falcon.shop.mapper.email.EmailMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl extends BaseServiceImpl<Email, EmailMapper> implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailTemplateService emailTemplateService;
    
    // 이메일 설정
    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Value("${email.from.name}")
    private String SENDER_NAME;
    
    @Override
    public boolean sendEmail(Email email) {
        try {
            // 이메일 발송 전 상태를 PENDING으로 설정
            email.setSendStatus(Email.SendStatus.PENDING);
            email.setSentAt(LocalDateTime.now());
            
            // 데이터베이스에 저장
            save(email);
            
            if (email.getIsHtml()) {
                // HTML 이메일 발송
                var mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                
                helper.setFrom(SENDER_EMAIL, SENDER_NAME);
                helper.setTo(email.getRecipientEmail());
                helper.setSubject(email.getSubject());
                helper.setText(email.getContent(), true);
                
                mailSender.send(mimeMessage);
            } else {
                // 텍스트 이메일 발송
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(SENDER_EMAIL);
                message.setTo(email.getRecipientEmail());
                message.setSubject(email.getSubject());
                message.setText(email.getContent());
                
                mailSender.send(message);
            }
            
            // 발송 성공 시 상태 업데이트
            email.setSendStatus(Email.SendStatus.SENT);
            email.setSentAt(LocalDateTime.now());
            updateById(email);
            
            log.info("이메일 발송 성공: {} -> {}", SENDER_EMAIL, email.getRecipientEmail());
            return true;
            
        } catch (Exception e) {
            // 발송 실패 시 상태 업데이트
            email.setSendStatus(Email.SendStatus.FAILED);
            email.setRetryCount(email.getRetryCount() + 1);
            email.setErrorMessage(e.getMessage());
            updateById(email);
            
            log.error("이메일 발송 실패: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendEmailWithTemplate(String templateType, String recipientEmail, String recipientName,
                                       Map<String, Object> variables, String relatedId) {
        try {
            // 템플릿 조회
            EmailTemplate template = emailTemplateService.getByType(templateType);
            if (template == null) {
                log.error("템플릿을 찾을 수 없습니다: {}", templateType);
                return false;
            }
            
            // 템플릿 변수 치환
            String subject = replaceTemplateVariables(template.getSubject(), variables);
            String content = replaceTemplateVariables(template.getContent(), variables);
            
            // 이메일 객체 생성
            Email email = new Email();
            email.setRecipientEmail(recipientEmail);
            email.setRecipientName(recipientName);
            email.setSenderEmail(SENDER_EMAIL);
            email.setSenderName(SENDER_NAME);
            email.setSubject(subject);
            email.setContent(content);
            email.setIsHtml(template.getIsHtml());
            email.setSendType(templateType);
            email.setRelatedId(relatedId);
            
            return sendEmail(email);
            
        } catch (Exception e) {
            log.error("템플릿 이메일 발송 실패: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean sendOrderEmail(String orderCode, String recipientEmail, String recipientName) {
        Map<String, Object> variables = Map.of(
            "orderCode", orderCode,
            "customerName", recipientName,
            "companyName", "Falcon Cartons"
        );
        
        return sendEmailWithTemplate("ORDER_CONFIRMATION", recipientEmail, recipientName, variables, orderCode);
    }
    
    @Override
    public boolean sendPaymentEmail(String orderCode, String paymentMethod, String recipientEmail, String recipientName) {
        Map<String, Object> variables = Map.of(
            "orderCode", orderCode,
            "customerName", recipientName,
            "paymentMethod", paymentMethod,
            "companyName", "Falcon Cartons"
        );
        
        return sendEmailWithTemplate("PAYMENT_GUIDE", recipientEmail, recipientName, variables, orderCode);
    }
    
    @Override
    public boolean sendTempPassword(String to, String username, String tempPassword) {
        Map<String, Object> variables = Map.of(
            "username", username,
            "tempPassword", tempPassword,
            "companyName", "Falcon Cartons"
        );
        
        return sendEmailWithTemplate("TEMP_PASSWORD", to, username, variables, null);
    }
    
    @Override
    public boolean sendSimpleEmail(String to, String subject, String content) {
        Email email = new Email();
        email.setRecipientEmail(to);
        email.setSenderEmail(SENDER_EMAIL);
        email.setSenderName(SENDER_NAME);
        email.setSubject(subject);
        email.setContent(content);
        email.setIsHtml(false);
        email.setSendType("SIMPLE");
        
        return sendEmail(email);
    }
    
    @Override
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        Email email = new Email();
        email.setRecipientEmail(to);
        email.setSenderEmail(SENDER_EMAIL);
        email.setSenderName(SENDER_NAME);
        email.setSubject(subject);
        email.setContent(htmlContent);
        email.setIsHtml(true);
        email.setSendType("HTML");
        
        return sendEmail(email);
    }
    
    @Override
    public boolean resendEmail(Long no) {
        try {
            Email email = getById(no);
            if (email == null) {
                log.error("이메일을 찾을 수 없습니다: {}", no);
                return false;
            }
            
            // 재발송 횟수 체크
            if (email.getRetryCount() >= 3) {
                log.error("재발송 횟수 초과: {}", no);
                return false;
            }
            
            return sendEmail(email);
            
        } catch (Exception e) {
            log.error("이메일 재발송 실패: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<Email> getEmailsBySendStatus(Email.SendStatus sendStatus) {
        return baseMapper.selectBySendStatus(sendStatus);
    }
    
    @Override
    public List<Email> getEmailsBySendType(String sendType) {
        return baseMapper.selectBySendType(sendType);
    }
    
    /**
     * 템플릿 변수 치환
     */
    private String replaceTemplateVariables(String template, Map<String, Object> variables) {
        String result = template;
        
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
}
