package com.falcon.shop.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 실제 Spring Mail을 사용하는 이메일 서비스
 * 현재는 의존성 문제로 Mock으로 구현
 * spring-boot-starter-mail 의존성 추가 후 실제 구현으로 교체 필요
 */
@Slf4j
@Service
public class RealEmailServiceImpl implements RealEmailService {

    @Value("${spring.mail.username:noreply@bunsoomarket.com}")
    private String fromAddress;

    @Value("${email.from.name:Falcon Cartons}")
    private String fromName;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendTempPassword(String to, String username, String tempPassword) {
        log.info("## 임시 비밀번호 이메일 발송 ##");
        log.info("수신자: {}, 사용자명: {}", to, username);

        String subject = "[Falcon Cartons] 임시 비밀번호 발송";
        String htmlContent = createTempPasswordHtml(username, tempPassword);

        return sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    public boolean sendEmail(String to, String subject, String content) {
        log.info("=== 이메일 발송 ===");
        log.info("발신자: {}", fromAddress);
        log.info("수신자: {}", to);
        log.info("제목: {}", subject);
        log.info("내용: {}", content);
        log.info("========================");
          
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            javaMailSender.send(message);
            log.info("이메일 발송 성공: {}", to);
            return true;
            
        } catch (MailException e) {
            log.error("이메일 발송 실패: {}, 오류: {}", to, e.getMessage());
            return false;
        }
        
    }

    @Override
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        log.info("=== HTML 이메일 발송 ===");
        log.info("발신자: {} ({})", fromName, fromAddress);
        log.info("수신자: {}", to);
        log.info("제목: {}", subject);
        log.info("HTML 내용 길이: {} characters", htmlContent.length());
         
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML 모드로 설정
            
            // mailcap 오류 방지를 위한 추가 설정
            mimeMessage.setHeader("Content-Type", "text/html; charset=UTF-8");
            
            if (htmlContent.contains("임시 비밀번호")) {
                String preview = extractPasswordFromHtml(htmlContent);
                log.info("임시 비밀번호: {}", preview);
            }
            
            javaMailSender.send(mimeMessage);
            log.info("HTML 이메일 발송 성공: {}", to);
            return true;
            
        } catch (MessagingException e) {
            log.error("HTML 이메일 발송 실패 (MessagingException): {}, 오류: {}", to, e.getMessage());
            // HTML 발송 실패시 텍스트로 fallback
            return sendTextEmail(to, subject, htmlToText(htmlContent));
        } catch (MailException e) {
            log.error("HTML 이메일 발송 실패 (MailException): {}, 오류: {}", to, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("이메일 발송 중 예상치 못한 오류: {}, 오류: {}", to, e.getMessage());
            // 예외 발생시 텍스트로 fallback
            return sendTextEmail(to, subject, htmlToText(htmlContent));
        }
    }
    
    /**
     * 텍스트 이메일 발송 (HTML 실패시 fallback)
     */
    private boolean sendTextEmail(String to, String subject, String textContent) {
        try {
            log.info("텍스트 이메일로 fallback 발송: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(textContent);
            
            javaMailSender.send(message);
            log.info("텍스트 이메일 발송 성공: {}", to);
            return true;
        } catch (Exception e) {
            log.error("텍스트 이메일 발송도 실패: {}, 오류: {}", to, e.getMessage());
            return false;
        }
    }
    
    /**
     * HTML을 간단한 텍스트로 변환
     */
    private String htmlToText(String htmlContent) {
        if (htmlContent == null) return "";
        
        // 기본적인 HTML 태그 제거
        return htmlContent
            .replaceAll("<[^>]+>", "") // HTML 태그 제거
            .replaceAll("&nbsp;", " ") // &nbsp; 변환
            .replaceAll("&amp;", "&")  // &amp; 변환
            .replaceAll("&lt;", "<")   // &lt; 변환
            .replaceAll("&gt;", ">")   // &gt; 변환
            .replaceAll("\\s+", " ")   // 연속된 공백을 하나로
            .trim();
    }

    /**
     * HTML에서 임시 비밀번호를 추출하는 헬퍼 메서드
     */
    private String extractPasswordFromHtml(String htmlContent) {
        try {
            // 간단한 정규식으로 비밀번호 추출
            int start = htmlContent.indexOf("<div class=\"password\">");
            if (start != -1) {
                start += "<div class=\"password\">".length();
                int end = htmlContent.indexOf("</div>", start);
                if (end != -1) {
                    return htmlContent.substring(start, end).trim();
                }
            }
        } catch (Exception e) {
            log.debug("비밀번호 추출 실패: {}", e.getMessage());
        }
        return "추출 실패";
    }

    /**
     * 임시 비밀번호 HTML 템플릿 생성
     * @param username 사용자명
     * @param tempPassword 임시 비밀번호
     * @return HTML 내용
     */
    private String createTempPasswordHtml(String username, String tempPassword) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>임시 비밀번호 발송</title>
                <style>
                    body {
                        font-family: 'Malgun Gothic', '맑은 고딕', Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        background-color: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .header {
                        text-align: center;
                        border-bottom: 2px solid #667eea;
                        padding-bottom: 20px;
                        margin-bottom: 30px;
                    }
                    .header h1 {
                        color: #667eea;
                        margin: 0;
                        font-size: 24px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .password-box {
                        background-color: #f8f9fa;
                        border: 2px solid #667eea;
                        border-radius: 8px;
                        padding: 20px;
                        text-align: center;
                        margin: 20px 0;
                    }
                    .password {
                        font-size: 24px;
                        font-weight: bold;
                        color: #667eea;
                        letter-spacing: 2px;
                        margin: 10px 0;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border: 1px solid #ffeaa7;
                        border-radius: 5px;
                        padding: 15px;
                        margin: 20px 0;
                        color: #856404;
                    }
                    .footer {
                        border-top: 1px solid #eee;
                        padding-top: 20px;
                        text-align: center;
                        color: #666;
                        font-size: 14px;
                    }
                    .button {
                        display: inline-block;
                        background-color: #667eea;
                        color: white;
                        padding: 12px 30px;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 20px 0;
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 Falcon Cartons</h1>
                        <p>임시 비밀번호 발송</p>
                    </div>
                    
                    <div class="content">
                        <p><strong>%s</strong>님, 안녕하세요!</p>
                        <p>요청하신 임시 비밀번호를 발송해드립니다.</p>
                        
                        <div class="password-box">
                            <p><strong>임시 비밀번호</strong></p>
                            <div class="password">%s</div>
                            <p style="font-size: 14px; color: #666; margin-top: 15px;">
                                위 비밀번호를 복사하여 로그인해주세요
                            </p>
                        </div>
                        
                        <div class="warning">
                            <p><strong>⚠️ 보안 안내</strong></p>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>로그인 후 <strong>반드시 비밀번호를 변경</strong>해주세요</li>
                                <li>임시 비밀번호는 타인에게 노출되지 않도록 주의하세요</li>
                                <li>비밀번호 변경은 마이페이지에서 가능합니다</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center;">
                            <a href="http://localhost:8080/login" class="button">로그인하기</a>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>본 메일은 발신전용 메일입니다.</p>
                        <p>문의사항이 있으시면 고객센터로 연락해주세요.</p>
                        <p style="margin-top: 15px;">
                            <strong>Falcon Cartons</strong><br>
                            이메일: support@bunsoomarket.com<br>
                            전화: 02-1234-5678
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, tempPassword);
    }

}
