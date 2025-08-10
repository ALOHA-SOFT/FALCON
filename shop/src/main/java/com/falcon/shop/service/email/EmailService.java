package com.falcon.shop.service.email;

/**
 * 이메일 발송 서비스 인터페이스
 */
public interface EmailService {
    
    /**
     * 임시 비밀번호 이메일 발송
     * @param to 수신자 이메일
     * @param username 사용자명
     * @param tempPassword 임시 비밀번호
     * @return 발송 성공 여부
     */
    boolean sendTempPassword(String to, String username, String tempPassword);
    
    /**
     * 일반 이메일 발송
     * @param to 수신자 이메일
     * @param subject 제목
     * @param content 내용
     * @return 발송 성공 여부
     */
    boolean sendEmail(String to, String subject, String content);
    
    /**
     * HTML 이메일 발송
     * @param to 수신자 이메일
     * @param subject 제목
     * @param htmlContent HTML 내용
     * @return 발송 성공 여부
     */
    boolean sendHtmlEmail(String to, String subject, String htmlContent);
}
