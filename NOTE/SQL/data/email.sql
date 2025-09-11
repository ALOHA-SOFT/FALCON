-- Active: 1751866834049@@falcon-db.cr8aiiek0cvi.eu-west-2.rds.amazonaws.com@3306@falcon
-- Delete existing templates
DELETE FROM email_templates;

INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    'Payment Guide Email',
    'PAYMENT_GUIDE',
    '[{{companyName}}] Payment Guide (Order No: {{orderCode}})',
    '<!DOCTYPE html>
<html style="margin: 0 !important; padding: 0 !important;">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin: 0px 0 !important; padding: 0 !important; background-color: #f4f4f4 !important; font-family: Arial, sans-serif !important;">
<!-- 메인 컨테이너 - 패딩 제거를 위해 직접 시작 -->
<table cellpadding="0" cellspacing="0" border="0" width="100%" style="margin: 50px 0 !important; padding: 0 !important; background-color: #f4f4f4 !important; font-family: Arial, sans-serif !important;">
  <tr>
    <td align="center" style="margin: 0 !important; padding: 0 !important; background-color: #f4f4f4 !important;">
      
      <!-- 실제 이메일 내용 - 600px 너비로 중앙 정렬 -->
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="margin: 0 auto !important; padding: 0 !important; background-color: #ffffff !important; border: 1px solid #ddd;">
        
        <!-- 헤더 -->
        <tr>
          <td style="background-color: #27ae60 !important; padding: 30px !important; text-align: center !important; margin: 0 !important;">
            <h1 style="color: #ffffff !important; margin: 0 !important; font-size: 28px !important; font-weight: bold !important;">{{companyName}}</h1>
            <p style="color: #ffffff !important; margin: 10px 0 0 0 !important; font-size: 16px !important;">Order Request Form Submitted Successfully</p>
          </td>
        </tr>
        
        <!-- 메인 콘텐츠 -->
        <tr>
          <td style="padding: 30px !important; background-color: #f9f9f9 !important; margin: 0 !important;">
            <p style="margin: 0 0 20px 0 !important; font-size: 16px !important; color: #333 !important;">
              <strong>{{customerName}}</strong>
            </p>
            
            <!-- 결제 정보 박스 -->
            <div style="background-color: #ffffff !important; border: 1px solid #27ae60 !important; border-left: 4px solid #27ae60 !important; margin: 20px 0 !important; padding: 20px !important;">
              <h3 style="margin: 0 0 15px 0 !important; color: #27ae60 !important; font-size: 18px !important;">Order Information</h3>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important;">
                <strong>Order No.:</strong> {{orderCode}}
              </p>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important;">
                <strong>Payment Method:</strong> {{paymentMethod}}
              </p>
            </div>
            
            <!-- 현금 결제 안내 박스 -->
            <div style="background-color: #fff3cd !important; border: 1px solid #ffeaa7 !important; margin: 20px 0 !important; padding: 20px !important;">
              <h4 style="margin: 0 0 10px 0 !important; color: #d35400 !important; font-size: 16px !important;">💰 Payment Instructions</h4>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important; line-height: 1.6 !important;">
                We will send you a separate payment instruction via email in the next 24 hours.
              </p>
            </div>
          </td>
        </tr>
        
        <!-- Footer -->
        <tr>
          <td style="padding: 20px !important; text-align: center !important; background-color: #e9e9f9 !important; margin: 0 !important;">
            <img src="https://falconcartons.com/img/logo.png" alt="{{companyName}}" style="max-width: 150px !important; height: 140px !important; margin-bottom: 10px !important;">
            <p style="margin: 0 0 5px 0 !important; font-size: 16px !important; font-weight: bold !important;">{{companyName}}</p>
            <p style="margin: 0 !important; font-size: 14px !important;">Email: info@falconcartons.com</p>
          </td>
        </tr>
        
      </table>
    </td>
  </tr>
</table>
</body>
</html>',
    1,
    '["customerName", "orderCode", "paymentMethod", "companyName"]',
    1,
    'Naver Compatible Cash/Coin payment method guide email',
    'system',
    'system',
    NOW(),
    NOW()
);

-- 2. Naver Compatible Order Confirmation Template
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    'Order Confirmation Email',
    'ORDER_CONFIRMATION', 
    '[{{companyName}}] Order Received (Order No: {{orderCode}})',
    '<table cellpadding="0" cellspacing="0" border="0" width="100%" style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
  <tr>
    <td align="center" style="padding: 20px;">
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="background-color: #ffffff; border: 1px solid #ddd;">
        <!-- Header -->
        <tr>
          <td style="background-color: #2c3e50; padding: 30px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">{{companyName}}</h1>
            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">Order Confirmation</p>
          </td>
        </tr>
        <!-- Main Content -->
        <tr>
          <td style="padding: 30px; background-color: #f9f9f9;">
            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">Hello <strong>{{customerName}}</strong>!</p>
            <p style="margin: 0 0 30px 0; font-size: 14px; color: #333; line-height: 1.6;">Thank you for your order. Please check your order details below.</p>
            
            <!-- Order Information Box -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #ffffff; border: 1px solid #2c3e50; margin: 20px 0;">
              <tr>
                <td style="padding: 20px; border-left: 4px solid #2c3e50;">
                  <h3 style="margin: 0 0 15px 0; color: #2c3e50; font-size: 18px;">Order Information</h3>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>Order Number:</strong> {{orderCode}}</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>Order Date:</strong> {{orderDate}}</p>
                </td>
              </tr>
            </table>
            
            <!-- Notice Message -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #e8f5e8; border: 1px solid #27ae60; margin: 20px 0;">
              <tr>
                <td style="padding: 20px;">
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">We will carefully prepare and ship your order.</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">If you have any shipping-related questions, please contact us anytime.</p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <!-- Footer -->
        <tr>
          <td style="padding: 20px; text-align: center;">
            <img src="https://falconcartons.com/img/logo.png" alt="{{companyName}}" style="max-width: 150px; margin-bottom: 10px;">
            <p style="margin: 0 0 5px 0; font-size: 16px; font-weight: bold;">{{companyName}}</p>
            <p style="margin: 0; font-size: 14px;">Email: info@falconcartons.com</p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>',
    1,
    '["customerName", "orderCode", "companyName", "orderDate"]',
    1,
    'Naver Compatible order confirmation email sent to customers when order is received',
    'system',
    'system',
    NOW(),
    NOW()
);

-- 3. Naver Compatible Temporary Password Template
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    'Temporary Password Email',
    'TEMP_PASSWORD',
    '[{{companyName}}] Temporary Password Sent',
    '<table cellpadding="0" cellspacing="0" border="0" width="100%" style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
  <tr>
    <td align="center" style="padding: 20px;">
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="background-color: #ffffff; border: 1px solid #ddd;">
        <!-- Header -->
        <tr>
          <td style="background-color: #e74c3c; padding: 30px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">🔐 {{companyName}}</h1>
            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">Temporary Password Sent</p>
          </td>
        </tr>
        <!-- Main Content -->
        <tr>
          <td style="padding: 30px; background-color: #f9f9f9;">
            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">Hello <strong>{{username}}</strong>!</p>
            <p style="margin: 0 0 30px 0; font-size: 14px; color: #333; line-height: 1.6;">We are sending you the temporary password you requested.</p>
            
            <!-- Temporary Password Box -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #ffffff; border: 2px solid #e74c3c; margin: 20px 0;">
              <tr>
                <td style="padding: 30px; text-align: center;">
                  <p style="margin: 0 0 15px 0; color: #333; font-size: 16px; font-weight: bold;">Temporary Password</p>
                  <p style="margin: 0; font-size: 28px; font-weight: bold; color: #e74c3c; letter-spacing: 3px;">{{tempPassword}}</p>
                </td>
              </tr>
            </table>
            
            <!-- Security Notice Box -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #fff3cd; border: 1px solid #ffeaa7; margin: 20px 0;">
              <tr>
                <td style="padding: 20px;">
                  <p style="margin: 0 0 15px 0; color: #d35400; font-size: 16px; font-weight: bold;">⚠️ Security Notice</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">• Please <strong>change your password</strong> after logging in</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">• Keep your temporary password secure and do not share with others</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">• You can change your password in My Page</p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <!-- Footer -->
        <tr>
          <td style="padding: 20px; text-align: center;">
            <img src="https://falconcartons.com/img/logo.png" alt="{{companyName}}" style="max-width: 150px; margin-bottom: 10px;">
            <p style="margin: 0 0 5px 0; font-size: 16px; font-weight: bold;">{{companyName}}</p>
            <p style="margin: 0; font-size: 14px;">Email: info@falconcartons.com</p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>',
    1,
    '["username", "tempPassword", "companyName"]',
    1,
    'Naver Compatible temporary password email',
    'system',
    'system',
    NOW(),
    NOW()
);

-- 4. 결제 완료 메일
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    'Payment Complete Email',
    'PAYMENT_COMPLETE',
    '[{{companyName}}] Payment Complete (Order No: {{orderCode}})',
    '<!DOCTYPE html>
<html style="margin: 0 !important; padding: 0 !important;">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body style="margin: 0 !important; padding: 0 !important; background-color: #f4f4f4 !important; font-family: Arial, sans-serif !important;">

<!-- 메인 컨테이너 - 패딩 제거를 위해 직접 시작 -->
<table cellpadding="0" cellspacing="0" border="0" width="100%" style="margin: 50px 0 !important; padding: 0 !important; background-color: #f4f4f4 !important; font-family: Arial, sans-serif !important;">
  <tr>
    <td align="center" style="margin: 0 !important; padding: 0 !important; background-color: #f4f4f4 !important;">
      
      <!-- 실제 이메일 내용 - 600px 너비로 중앙 정렬 -->
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="margin: 0 auto !important; padding: 0 !important; background-color: #ffffff !important; border: 1px solid #ddd;">
        
        <!-- 헤더 -->
        <tr>
          <td style="background-color: #2269f5 !important; padding: 30px !important; text-align: center !important; margin: 0 !important;">
            <h1 style="color: #ffffff !important; margin: 0 !important; font-size: 28px !important; font-weight: bold !important;">{{companyName}}</h1>
            <p style="color: #ffffff !important; margin: 10px 0 0 0 !important; font-size: 16px !important;">Payment Completed Successfully</p>
          </td>
        </tr>
        
        <!-- 메인 콘텐츠 -->
        <tr>
          <td style="padding: 30px !important; background-color: #f9f9f9 !important; margin: 0 !important;">
            <p style="margin: 0 0 20px 0 !important; font-size: 16px !important; color: #333 !important;">
              Hello <strong>{{customerName}}</strong>!
            </p>
            
            <p style="margin: 0 0 30px 0 !important; font-size: 14px !important; color: #666 !important; line-height: 1.6 !important;">
              Your payment has been successfully completed. We will begin processing your order.
            </p>
            
            <!-- 결제 정보 박스 -->
            <div style="background-color: #ffffff !important; border: 1px solid #2269f5 !important; border-left: 4px solid #2269f5 !important; margin: 20px 0 !important; padding: 20px !important;">
              <h3 style="margin: 0 0 15px 0 !important; color: #2269f5 !important; font-size: 18px !important;">💳 Payment Information</h3>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important;">
                <strong>Order No.:</strong> {{orderCode}}
              </p>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important;">
                <strong>Payment Method:</strong> {{paymentMethod}}
              </p>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important;">
                <strong>Payment Amount:</strong> {{totalAmount}}
              </p>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important;">
                <strong>Payment Date:</strong> {{paymentDate}}
              </p>
            </div>
            
            <!-- 주문 상태 안내 박스 -->
            <div style="background-color: #e8f4fd !important; border: 1px solid #2269f5 !important; margin: 20px 0 !important; padding: 20px !important;">
              <h4 style="margin: 0 0 10px 0 !important; color: #1a5bb8 !important; font-size: 16px !important;">📦 Next Steps</h4>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important; line-height: 1.6 !important;">
                • Order confirmation and product preparation is in progress<br>
                • We will send you a shipping notification email once your order is ready for dispatch<br>
                • Please feel free to contact us if you have any questions
              </p>
            </div>
            
            <!-- 감사 메시지 -->
            <div style="background-color: #fff9e6 !important; border: 1px solid #f39c12 !important; margin: 20px 0 !important; padding: 20px !important; text-align: center !important;">
              <h4 style="margin: 0 0 10px 0 !important; color: #d68910 !important; font-size: 16px !important;">🙏 Thank You!</h4>
              <p style="margin: 5px 0 !important; font-size: 14px !important; color: #333 !important; line-height: 1.6 !important;">
                Thank you for choosing {{companyName}}.<br>
                We are committed to providing you with the best service.
              </p>
            </div>
          </td>
        </tr>
        
        <!-- Footer -->
        <tr>
          <td style="padding: 20px !important; text-align: center !important; background-color: #e9e9f9 !important; margin: 0 !important;">
            <img src="https://falconcartons.com/img/logo.png" alt="{{companyName}}" style="max-width: 150px !important; height: 140px !important; margin-bottom: 10px !important;">
            <p style="margin: 0 0 5px 0 !important; font-size: 16px !important; font-weight: bold !important;">{{companyName}}</p>
            <p style="margin: 0 !important; font-size: 14px !important;">Email: info@falconcartons.com</p>
            <p style="margin: 5px 0 0 0 !important; font-size: 12px !important; color: #666 !important;">
              Customer Service: 1588-0000 | Business Hours: 09:00 - 18:00 (Mon-Fri)
            </p>
          </td>
        </tr>
        
      </table>
    </td>
  </tr>
</table>

</body>
</html>

',
    1,
    '["customerName", "orderCode", "paymentMethod", "companyName"]',
    1,
    'Payment Complete Email',
    'system',
    'system',
    NOW(),
    NOW()
);

-- Insert confirmation
SELECT name, type, is_html, description FROM email_templates 
;