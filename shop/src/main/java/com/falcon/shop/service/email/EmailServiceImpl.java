package com.falcon.shop.service.email;

import java.math.BigDecimal;
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
import com.falcon.shop.domain.shop.OrderItem;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.domain.users.Users;
import com.falcon.shop.mapper.email.EmailMapper;
import com.falcon.shop.mapper.shop.OrderItemMapper;
import com.falcon.shop.mapper.users.UserMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailServiceImpl extends BaseServiceImpl<Email, EmailMapper> implements EmailService {
    
    @Autowired private JavaMailSender mailSender;
    @Autowired private EmailTemplateService emailTemplateService;
    @Autowired private UserMapper userMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    
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
            // log.info("email - id : {}", email.getId());

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
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("orderCode", orderCode);
        variables.put("customerName", recipientName);
        variables.put("companyName", "Falcon Cartons");

        return sendEmailWithTemplate("ORDER_CONFIRMATION", recipientEmail, recipientName, variables, orderCode);
    }


    /**
     * 결제안내메일에 사용되는 주문 상세 HTML 테이블 생성
     * @param order
     * @param orderItems
     * @return
     */
    public String createOrderDetailHTML(Orders order, List<OrderItem> orderItems) {
        StringBuilder table = new StringBuilder();
        table.append("<div style=\"background-color: #ffffff !important; border: 1px solid #27ae60 !important; border-left: 4px solid #27ae60 !important; margin: 20px 0 !important; padding: 20px !important;\">");
        table.append("<h3 style=\"margin: 0 0 15px 0 !important; color: #27ae60 !important; font-size: 18px !important;\">Order Details</h3>");
        table.append("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" style=\"border-collapse: collapse !important;\">");
        table.append("<thead>");
        table.append("<tr style=\"background-color: #f8f9fa !important;\">");
        table.append("<th style=\"padding: 10px !important; text-align: left !important; border-bottom: 2px solid #27ae60 !important; font-size: 14px !important; color: #333 !important;\">Product Name</th>");
        table.append("<th style=\"padding: 10px !important; text-align: center !important; border-bottom: 2px solid #27ae60 !important; font-size: 14px !important; color: #333 !important;\">Unit Price</th>");
        table.append("<th style=\"padding: 10px !important; text-align: center !important; border-bottom: 2px solid #27ae60 !important; font-size: 14px !important; color: #333 !important;\">Quantity</th>");
        table.append("<th style=\"padding: 10px !important; text-align: right !important; border-bottom: 2px solid #27ae60 !important; font-size: 14px !important; color: #333 !important;\">Price</th>");
        table.append("</tr>");
        table.append("</thead>");
        table.append("<tbody>");

        // 주문 상품들을 테이블 행으로 추가
        for (OrderItem item : orderItems) {
            table.append("<tr>");
            table.append("<td style=\"padding: 10px !important; border-bottom: 1px solid #eee !important; font-size: 14px !important; color: #333 !important;\">");
            table.append(item.getProduct() != null ? item.getProduct().getName() : "Product");
            table.append("</td>");
            table.append("<td style=\"padding: 10px !important; text-align: center !important; border-bottom: 1px solid #eee !important; font-size: 14px !important; color: #333 !important;\">");
            table.append("£").append(item.getPrice());
            table.append("</td>");
            table.append("<td style=\"padding: 10px !important; text-align: center !important; border-bottom: 1px solid #eee !important; font-size: 14px !important; color: #333 !important;\">");
            table.append(item.getQuantity());
            table.append("</td>");
            table.append("<td style=\"padding: 10px !important; text-align: right !important; border-bottom: 1px solid #eee !important; font-size: 14px !important; color: #333 !important;\">");
            table.append("£").append(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            table.append("</td>");
            table.append("</tr>");
        }

        table.append("</tbody>");
        table.append("<tfoot>");
        table.append("<tr>");
        table.append("<td colspan=\"3\" style=\"padding: 10px !important; text-align: right !important; border-bottom: 1px solid #eee !important; font-size: 14px !important; color: #666 !important; font-weight: bold !important;\">Shipping Cost</td>");
        table.append("<td style=\"padding: 10px !important; text-align: right !important; border-bottom: 1px solid #eee !important; font-size: 14px !important; color: #666 !important; font-weight: bold !important;\">");
        table.append("£").append(order.getShipPrice() != null ? order.getShipPrice() : BigDecimal.ZERO);
        table.append("</td>");
        table.append("</tr>");
        table.append("<tr style=\"background-color: #27ae60 !important;\">");
        table.append("<td colspan=\"3\" style=\"padding: 12px 10px !important; font-weight: bold !important; color: #ffffff !important; font-size: 16px !important;\">Total Amount</td>");
        table.append("<td style=\"padding: 12px 10px !important; text-align: right !important; font-weight: bold !important; color: #ffffff !important; font-size: 16px !important;\">");
        table.append("£").append(order.getTotalPrice());
        table.append("</td>");
        table.append("</tr>");
        table.append("</tfoot>");
        table.append("</table>");
        table.append("</div>");

        return table.toString();
    }
    
    @Override
    public boolean sendPaymentEmail(Orders order, String paymentMethod, String recipientEmail, String recipientName) {

        Users user = userMapper.selectById(order.getUserNo());
        String userId = user.getId();

        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("host", host);
        variables.put("orderCode", order.getCode());
        variables.put("orderId", order.getId());
        variables.put("userId", userId);
        variables.put("customerName", recipientName);
        variables.put("paymentMethod", paymentMethod);
        variables.put("companyName", "Falcon Cartons");

        // orderCode 로 Orders 조회하고, List<OrderItem> 등 주문 상세 정보를 HTML 테이블 형태로 생성
        log.info("#############################################");
        log.info("order : {}", order);
        
        // 상품 아이템 목록 조회
        List<OrderItem> orderItems = orderItemMapper.selectListByOrderNo(order.getNo());
        log.info("orderItems : {}", orderItems);
        
        // 주문 상품 정보를 HTML 테이블로 생성
        String orderDetailsTable = createOrderDetailHTML(order, orderItems);
        log.info("orderDetailsTable : {}", orderDetailsTable);
        
        // variables에 주문 상품 테이블 추가
        variables.put("orderDetailsTable", orderDetailsTable);
        log.info("#############################################");
        
        return sendEmailWithTemplate("PAYMENT_GUIDE", recipientEmail, recipientName, variables, order.getCode());
    }
    
    @Override
    public boolean sendTempPassword(String to, String username, String tempPassword) {
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("username", username);
        variables.put("tempPassword", tempPassword);
        variables.put("companyName", "Falcon Cartons");
        
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

        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("host", host);
        variables.put("orderCode", order.getCode());
        variables.put("orderId", order.getId());
        variables.put("userId", userId);
        variables.put("customerName", recipientName);
        variables.put("paymentMethod", paymentMethod);
        variables.put("companyName", "Falcon Cartons");
        variables.put("totalAmount", order.getTotalPrice());
        variables.put("paymentDate", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

        return sendEmailWithTemplate("PAYMENT_COMPLETE", recipientEmail, recipientName, variables, order.getCode());
    }
    
    @Override
    public boolean sendUpdateOrderStatus(Orders order, String orderStatus, String recipientEmail, String recipientName) {
        Users user = userMapper.selectById(order.getUserNo());
        String userId = user.getId();

        // 상태별 변수 설정
        Map<String, Object> statusVariables = getOrderStatusVariables(orderStatus);
        
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("host", host);
        variables.put("orderCode", order.getCode());
        variables.put("orderId", order.getId());
        variables.put("userId", userId);
        variables.put("customerName", recipientName);
        variables.put("companyName", "Falcon Cartons");
        variables.put("orderDate", new java.text.SimpleDateFormat("dd/MM/yyyy").format(order.getCreatedAt()));
        variables.put("orderStatus", orderStatus);
        variables.put("statusUpdateDate", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        
        // 상태별 변수 추가
        variables.putAll(statusVariables);
        
        return sendEmailWithTemplate("ORDER_STATUS_CHANGE", recipientEmail, recipientName, variables, order.getCode());
    }
    
    @Override
    public boolean sendUpdateShipmentStatus(Orders order, String shippingStatus, 
                                          String trackingNo, String shipCompany, String deliveryMethod,
                                          String recipientEmail, String recipientName) {
        Users user = userMapper.selectById(order.getUserNo());
        String userId = user.getId();

        // 상태별 변수 설정
        Map<String, Object> statusVariables = getShippingStatusVariables(order.getStatus());
        
        Map<String, Object> variables = new java.util.HashMap<>();
        variables.put("host", host);
        variables.put("orderCode", order.getCode());
        variables.put("orderId", order.getId());
        variables.put("userId", userId);
        variables.put("customerName", recipientName);
        variables.put("companyName", "Falcon Cartons");
        variables.put("orderDate", new java.text.SimpleDateFormat("dd/MM/yyyy").format(order.getCreatedAt()));
        variables.put("orderStatus", shippingStatus);
        variables.put("statusUpdateDate", new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        variables.put("trackingNo", trackingNo != null ? trackingNo : "");
        variables.put("shipCompany", shipCompany != null ? shipCompany : "");
        variables.put("deliveryMethod", deliveryMethod != null ? deliveryMethod : "");

        // 상태별 변수 추가
        variables.putAll(statusVariables);
        
        return sendEmailWithTemplate("SHIPMENT_STATUS_CHANGE", recipientEmail, recipientName, variables, order.getCode());
    }
    
    /**
     * 주문 상태별 변수 설정
     */
    private Map<String, Object> getOrderStatusVariables(String orderStatus) {
        Map<String, Object> variables = new java.util.HashMap<>();
        
        switch (orderStatus) {
            case "PAYMENT_PENDING":
            case "결제대기":
                variables.put("statusColor", "#f39c12");
                variables.put("statusIcon", "⏳");
                variables.put("statusTitle", "Payment Pending");
                variables.put("statusMessage", "Your order is waiting for payment confirmation.");
                variables.put("statusBgColor", "#fff3cd");
                variables.put("statusBorderColor", "#ffeaa7");
                variables.put("statusTextColor", "#d68910");
                variables.put("detailTitle", "What's Next?");
                variables.put("statusDetails", "• Please complete your payment as soon as possible<br>• Payment methods: Credit Card, Bank Transfer<br>• Order will be automatically cancelled after 7 days without payment");
                break;
                
            case "PAYMENT_COMPLETED":
            case "결제완료":
                variables.put("statusColor", "#28a745");
                variables.put("statusIcon", "✅");
                variables.put("statusTitle", "Payment Completed");
                variables.put("statusMessage", "Your payment has been successfully processed and your order is being prepared.");
                variables.put("statusBgColor", "#d4edda");
                variables.put("statusBorderColor", "#c3e6cb");
                variables.put("statusTextColor", "#155724");
                variables.put("detailTitle", "Processing Your Order");
                variables.put("statusDetails", "• Payment confirmed and processed<br>• Order preparation will begin shortly<br>• You will receive shipping notification once dispatched");
                break;
                
            case "ORDER_CANCELLED":
            case "주문취소":
                variables.put("statusColor", "#dc3545");
                variables.put("statusIcon", "❌");
                variables.put("statusTitle", "Order Cancelled");
                variables.put("statusMessage", "Your order has been cancelled as requested.");
                variables.put("statusBgColor", "#f8d7da");
                variables.put("statusBorderColor", "#f5c6cb");
                variables.put("statusTextColor", "#721c24");
                variables.put("detailTitle", "Cancellation Details");
                variables.put("statusDetails", "• Order has been successfully cancelled<br>• If payment was made, refund will be processed<br>• Refund may take 3-5 business days to appear in your account");
                break;
                
            case "REFUND_COMPLETED":
            case "환불완료":
                variables.put("statusColor", "#6f42c1");
                variables.put("statusIcon", "💰");
                variables.put("statusTitle", "Refund Completed");
                variables.put("statusMessage", "Your refund has been processed successfully.");
                variables.put("statusBgColor", "#e2e3f3");
                variables.put("statusBorderColor", "#d0d1e3");
                variables.put("statusTextColor", "#493c74");
                variables.put("detailTitle", "Refund Information");
                variables.put("statusDetails", "• Refund has been processed to your original payment method<br>• Amount may take 3-5 business days to appear in your account<br>• Please contact your bank if you don't see the refund after this period");
                break;
                
            default:
                // 기본값
                variables.put("statusColor", "#6c757d");
                variables.put("statusIcon", "📋");
                variables.put("statusTitle", "Order Status Updated");
                variables.put("statusMessage", "Your order status has been updated.");
                variables.put("statusBgColor", "#f8f9fa");
                variables.put("statusBorderColor", "#dee2e6");
                variables.put("statusTextColor", "#495057");
                variables.put("detailTitle", "Status Information");
                variables.put("statusDetails", "• Your order status has been updated<br>• Please contact customer service for more details");
                break;
        }
        
        return variables;
    }
    
    /**
     * 배송 상태별 변수 설정
     */
    private Map<String, Object> getShippingStatusVariables(String shippingStatus) {
        Map<String, Object> variables = new java.util.HashMap<>();
        
        switch (shippingStatus) {
            case "PREPARING_SHIPMENT":
            case "배송준비중":
                variables.put("statusColor", "#61acfc");
                variables.put("statusIcon", "📦");
                variables.put("statusTitle", "Preparing for Shipment");
                variables.put("statusMessage", "Your order is being carefully prepared for shipment.");
                variables.put("statusBgColor", "#d1ecf1");
                variables.put("statusBorderColor", "#bee5eb");
                variables.put("statusTextColor", "#0c5460");
                variables.put("detailTitle", "Preparation Status");
                variables.put("statusDetails", "• Items are being picked and packed<br>• Quality check in progress<br>• Shipping label will be generated soon");
                variables.put("shippingDisplay", "display: none !important;");
                break;
                
            case "SHIPMENT_STARTED":
            case "배송시작":
                variables.put("statusColor", "#61acfc");
                variables.put("statusIcon", "🚛");
                variables.put("statusTitle", "Shipment Started");
                variables.put("statusMessage", "Your order has been dispatched and is on its way to you!");
                variables.put("statusBgColor", "#e3f2fd");
                variables.put("statusBorderColor", "#bbdefb");
                variables.put("statusTextColor", "#1565c0");
                variables.put("detailTitle", "In Transit");
                variables.put("statusDetails", "• Package has left our warehouse<br>• Tracking information is now available<br>• Estimated delivery time provided below");
                variables.put("shippingDisplay", "display: block !important;");
                break;
                
            case "IN_TRANSIT":
            case "배송중":
                variables.put("statusColor", "#61acfc");
                variables.put("statusIcon", "🚚");
                variables.put("statusTitle", "In Transit");
                variables.put("statusMessage", "Your package is currently in transit to your delivery address.");
                variables.put("statusBgColor", "#e8eaf6");
                variables.put("statusBorderColor", "#c5cae9");
                variables.put("statusTextColor", "#283593");
                variables.put("detailTitle", "Delivery Progress");
                variables.put("statusDetails", "• Package is en route to destination<br>• Track your package using the tracking number below<br>• Please ensure someone is available to receive the package");
                variables.put("shippingDisplay", "display: block !important;");
                break;
                
            case "DELIVERED":
            case "배송완료":
                variables.put("statusColor", "#4caf50");
                variables.put("statusIcon", "🎉");
                variables.put("statusTitle", "Order Delivered");
                variables.put("statusMessage", "Your order has been successfully delivered! We hope you're satisfied with your purchase.");
                variables.put("statusBgColor", "#e8f5e8");
                variables.put("statusBorderColor", "#c8e6c9");
                variables.put("statusTextColor", "#2e7d32");
                variables.put("detailTitle", "Delivery Completed");
                variables.put("statusDetails", "• Package delivered successfully<br>• Please check your items and contact us if anything is missing<br>• Thank you for your business!");
                variables.put("shippingDisplay", "display: block !important;");
                break;
                
            default:
                // 기본값
                variables.put("statusColor", "#6c757d");
                variables.put("statusIcon", "📦");
                variables.put("statusTitle", "Shipment Status Updated");
                variables.put("statusMessage", "Your shipment status has been updated.");
                variables.put("statusBgColor", "#f8f9fa");
                variables.put("statusBorderColor", "#dee2e6");
                variables.put("statusTextColor", "#495057");
                variables.put("detailTitle", "Status Information");
                variables.put("statusDetails", "• Your shipment status has been updated<br>• Please contact customer service for more details");
                variables.put("shippingDisplay", "display: none !important;");
                break;
        }
        
        return variables;
    }
}
