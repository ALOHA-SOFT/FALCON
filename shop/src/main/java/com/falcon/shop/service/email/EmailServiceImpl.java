package com.falcon.shop.service.email;

import java.util.Date;
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
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.mapper.email.EmailMapper;
import com.falcon.shop.mapper.users.UserMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl extends BaseServiceImpl<Email, EmailMapper> implements EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private EmailTemplateService emailTemplateService;

    @Autowired
    private UserMapper userMapper;
    
    // 이메일 설정
    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Value("${email.from.name}")
    private String SENDER_NAME;

    @Value("${host}")
    private String host;

    
    @Override
    public boolean sendEmail(Email email) {
        try {
            // 이메일 발송 전 상태를 PENDING으로 설정
            email.setSendStatus("PENDING");
            email.setSendAt(new Date());
            
            // 데이터베이스에 저장
            // if( email.getId() == null) {
                log.info("이메일 저장: {} -> {}", SENDER_EMAIL, email.getRecipientEmail());
                save(email);
            // }

            if (email.getIsHtml()) {
                // HTML 이메일 발송 (네이버 호환성 개선)
                var mimeMessage = mailSender.createMimeMessage();
                // 멀티파트 설정: true로 설정하여 HTML+텍스트 지원
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
                
                helper.setFrom(SENDER_EMAIL, SENDER_NAME);
                helper.setTo(email.getRecipientEmail());
                helper.setSubject(email.getSubject());
                
                // 네이버 호환을 위한 멀티파트 설정
                String htmlContent = wrapHtmlForNaver(createNaverSafeHtml(email.getContent()));
                String textContent = stripHtmlTags(email.getContent());
                
                // HTML과 텍스트 버전 모두 설정 (네이버 호환성 향상)
                helper.setText(textContent, htmlContent);
                
                // 네이버 호환 헤더 추가
                mimeMessage.setHeader("MIME-Version", "1.0");
                mimeMessage.setHeader("X-Mailer", "Falcon Cartons Mailer");
                mimeMessage.setHeader("X-Priority", "3");
                mimeMessage.setHeader("X-MSMail-Priority", "Normal");
                
                log.info("네이버 호환 HTML 이메일 발송: {} -> {}", SENDER_EMAIL, email.getRecipientEmail());
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
            
            email.setSendStatus("SENT");
            log.info("이메일 발송 성공: {} -> {}", SENDER_EMAIL, email.getRecipientEmail());
            log.info("email : {}", email);
            log.info("email - id : {}", email.getId());

            // 발송 성공 시 상태 업데이트
            boolean statusUpdateResult = updateById(email);
            if (!statusUpdateResult) {
                log.error("이메일 발송 상태 업데이트 실패: {} -> {}", SENDER_EMAIL, email.getRecipientEmail());
            }

            log.info("이메일 발송 성공: {} -> {}", SENDER_EMAIL, email.getRecipientEmail());
            return true;
            
        } catch (Exception e) {
            // 발송 실패 시 상태 업데이트
            email.setSendStatus("FAILED");
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
            email.setTemplateNo(template.getNo());
            
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
    public boolean sendPaymentEmail(Orders order, String paymentMethod, String recipientEmail, String recipientName) {

        Users user = userMapper.selectById(order.getUserNo());
        String userId = user.getId();

        Map<String, Object> variables = Map.of(
            "host", host,
            "orderCode", order.getCode(),
            "orderId", order.getId(),
            "userId", userId,
            "customerName", recipientName,
            "paymentMethod", paymentMethod,
            "companyName", "Falcon Cartons"
        );
        
        return sendEmailWithTemplate("PAYMENT_GUIDE", recipientEmail, recipientName, variables, order.getCode());
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
            // if (email.getRetryCount() >= 3) {
            //     log.error("재발송 횟수 초과: {}", no);
            //     return false;            // }
            return sendEmail(email);
            
        } catch (Exception e) {
            log.error("이메일 재발송 실패: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<Email> getEmailsBySendStatus(String sendStatus) {
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
    
    /**
     * 네이버 메일 호환을 위한 HTML 래핑
     * 네이버는 완전한 HTML 구조와 테이블 기반 레이아웃을 선호함
     */
    private String wrapHtmlForNaver(String content) {
        // 이미 완전한 HTML 문서인지 확인
        if (content.toLowerCase().contains("<!doctype") || content.toLowerCase().contains("<html")) {
            // 기존 HTML에서 네이버 비호환 요소들을 안전한 형태로 변환
            String naverSafeContent = content;
            
            // CSS 스타일을 인라인으로 변환하거나 안전한 형태로 수정
            naverSafeContent = naverSafeContent.replaceAll("border-radius:\\s*[^;]+;", ""); // 네이버에서 지원하지 않는 속성 제거
            naverSafeContent = naverSafeContent.replaceAll("box-shadow:\\s*[^;]+;", ""); // 네이버에서 지원하지 않는 속성 제거
            
            // 테이블 기반 레이아웃으로 감싸기
            StringBuilder wrappedHtml = new StringBuilder();
            wrappedHtml.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
            wrappedHtml.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            wrappedHtml.append("<head>\n");
            wrappedHtml.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
            wrappedHtml.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
            wrappedHtml.append("</head>\n");
            wrappedHtml.append("<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;\">\n");
            wrappedHtml.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"background-color: #f4f4f4;\">\n");
            wrappedHtml.append("  <tr>\n");
            wrappedHtml.append("    <td align=\"center\">\n");
            
            // 기존 content의 body 내용만 추출
            String bodyContent = naverSafeContent;
            int bodyStart = bodyContent.indexOf("<body");
            int bodyEnd = bodyContent.lastIndexOf("</body>");
            if (bodyStart >= 0 && bodyEnd >= 0) {
                int contentStart = bodyContent.indexOf(">", bodyStart) + 1;
                bodyContent = bodyContent.substring(contentStart, bodyEnd);
            }
            
            wrappedHtml.append(bodyContent);
            wrappedHtml.append("    </td>\n");
            wrappedHtml.append("  </tr>\n");
            wrappedHtml.append("</table>\n");
            wrappedHtml.append("</body>\n");
            wrappedHtml.append("</html>");
            
            return wrappedHtml.toString();
        }
        
        // 네이버 호환 HTML 구조로 래핑 (테이블 기반 레이아웃)
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
        html.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
        html.append("<head>\n");
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
        html.append("<title>Falcon Cartons</title>\n");
        html.append("</head>\n");
        html.append("<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;\">\n");
        html.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"background-color: #f4f4f4;\">\n");
        html.append("  <tr>\n");
        html.append("    <td align=\"center\">\n");
        html.append("      <table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"600\" style=\"background-color: #ffffff; margin: 20px auto;\">\n");
        html.append("        <tr>\n");
        html.append("          <td style=\"padding: 20px; font-family: Arial, sans-serif; font-size: 14px; line-height: 1.6; color: #333333;\">\n");
        html.append(content);
        html.append("          </td>\n");
        html.append("        </tr>\n");
        html.append("      </table>\n");
        html.append("    </td>\n");
        html.append("  </tr>\n");
        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        return html.toString();
    }
    
    /**
     * HTML 태그 제거하여 순수 텍스트 생성
     */
    private String stripHtmlTags(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        // HTML 태그 제거
        String text = html.replaceAll("<[^>]*>", "");
        
        // HTML 엔티티 디코딩
        text = text.replace("&nbsp;", " ");
        text = text.replace("&amp;", "&");
        text = text.replace("&lt;", "<");
        text = text.replace("&gt;", ">");
        text = text.replace("&quot;", "\"");
        text = text.replace("&#39;", "'");
        
        // 연속된 공백과 줄바꿈 정리
        text = text.replaceAll("\\s+", " ");
        text = text.trim();
        
        return text;
    }
    
    /**
     * 네이버 메일용 안전한 HTML 생성
     * 모든 스타일을 인라인으로 적용하고 네이버에서 지원하지 않는 속성 제거
     */
    private String createNaverSafeHtml(String content) {
        // CSS 스타일 블록을 인라인 스타일로 변환
        String naverSafeContent = content;
        
        // 네이버에서 지원하지 않는 CSS 속성들 제거
        naverSafeContent = naverSafeContent.replaceAll("border-radius:\\s*[^;]+;", "");
        naverSafeContent = naverSafeContent.replaceAll("box-shadow:\\s*[^;]+;", "");
        naverSafeContent = naverSafeContent.replaceAll("text-shadow:\\s*[^;]+;", "");
        naverSafeContent = naverSafeContent.replaceAll("transform:\\s*[^;]+;", "");
        naverSafeContent = naverSafeContent.replaceAll("transition:\\s*[^;]+;", "");
        
        // <style> 태그 제거하고 대신 인라인 스타일로 변환
        naverSafeContent = naverSafeContent.replaceAll("<style[^>]*>.*?</style>", "");
        
        return naverSafeContent;
    }

    @Override
    public boolean sendPaymentCompleteEmail(Orders order, String paymentMethod, String recipientEmail, String recipientName) {

        Users user = userMapper.selectById(order.getUserNo());
        String userId = user.getId();

        Map<String, Object> variables = Map.of(
            "host", host,
            "orderCode", order.getCode(),
            "orderId", order.getId(),
            "userId", userId,
            "customerName", recipientName,
            "paymentMethod", paymentMethod,
            "companyName", "Falcon Cartons",
            "totalAmount", order.getTotalPrice(),
            "paymentDate", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())
        );

        return sendEmailWithTemplate("PAYMENT_COMPLETE", recipientEmail, recipientName, variables, order.getCode());
    }
}
