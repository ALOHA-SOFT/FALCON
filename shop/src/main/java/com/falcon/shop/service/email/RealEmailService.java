package com.falcon.shop.service.email;


public interface RealEmailService {

    boolean sendTempPassword(String to, String username, String tempPassword);

    boolean sendEmail(String to, String subject, String content);

    boolean sendHtmlEmail(String to, String subject, String htmlContent);
}
