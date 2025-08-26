package com.falcon.shop.service.email;

import java.util.List;
import java.util.Map;

import com.falcon.shop.domain.email.Email;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.service.BaseService;

/**
 * 이메일 발송 서비스 인터페이스
 */
public interface EmailService extends BaseService<Email> {
    
    /**
     * 이메일 발송
     * @param email 이메일 정보
     * @return 발송 성공 여부
     */
    boolean sendEmail(Email email);
    
    /**
     * 템플릿을 사용한 이메일 발송
     * @param templateType 템플릿 타입
     * @param recipientEmail 받는사람 이메일
     * @param recipientName 받는사람 이름
     * @param variables 템플릿 변수
     * @param relatedId 관련 ID
     * @return 발송 성공 여부
     */
    boolean sendEmailWithTemplate(String templateType, String recipientEmail, String recipientName, 
                                 Map<String, Object> variables, String relatedId);
    
    /**
     * 주문 관련 이메일 발송
     * @param orderCode 주문코드
     * @param recipientEmail 받는사람 이메일
     * @param recipientName 받는사람 이름
     * @return 발송 성공 여부
     */
    boolean sendOrderEmail(String orderCode, String recipientEmail, String recipientName);
    
    /**
     * 결제 관련 이메일 발송
     * @param orderCode 주문코드
     * @param paymentMethod 결제방식
     * @param recipientEmail 받는사람 이메일
     * @param recipientName 받는사람 이름
     * @return 발송 성공 여부
     */
    boolean sendPaymentEmail(Orders order, String paymentMethod, String recipientEmail, String recipientName);
    
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
    boolean sendSimpleEmail(String to, String subject, String content);
    
    /**
     * HTML 이메일 발송
     * @param to 수신자 이메일
     * @param subject 제목
     * @param htmlContent HTML 내용
     * @return 발송 성공 여부
     */
    boolean sendHtmlEmail(String to, String subject, String htmlContent);
    
    /**
     * 발송 실패한 이메일 재발송
     * @param no 이메일 번호
     * @return 재발송 성공 여부
     */
    boolean resendEmail(Long no);
    
    /**
     * 발송 상태별 이메일 목록 조회
     * @param sendStatus 발송 상태
     * @return 이메일 목록
     */
    List<Email> getEmailsBySendStatus(String sendStatus);
    
    /**
     * 발송 타입별 이메일 목록 조회
     * @param sendType 발송 타입
     * @return 이메일 목록
     */
    List<Email> getEmailsBySendType(String sendType);
}
