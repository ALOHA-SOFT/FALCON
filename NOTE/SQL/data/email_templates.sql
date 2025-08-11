-- 이메일 템플릿 기본 데이터 삽입

-- 1. 주문 확인 템플릿
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    '주문 확인 이메일',
    'ORDER_CONFIRMATION', 
    '[{{companyName}}] 주문이 접수되었습니다 (주문번호: {{orderCode}})',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>주문 확인</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background-color: #f9f9f9; }
        .order-info { background-color: white; padding: 15px; margin: 15px 0; border-radius: 5px; }
        .footer { text-align: center; padding: 20px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{{companyName}}</h1>
            <p>주문 확인서</p>
        </div>
        <div class="content">
            <p><strong>{{customerName}}</strong>님, 안녕하세요!</p>
            <p>주문해주셔서 감사합니다. 주문 내용을 확인해주세요.</p>
            
            <div class="order-info">
                <h3>주문 정보</h3>
                <p><strong>주문번호:</strong> {{orderCode}}</p>
                <p><strong>주문일시:</strong> {{orderDate}}</p>
            </div>
            
            <p>주문하신 상품을 정성껏 준비하여 배송해드리겠습니다.</p>
            <p>배송 관련 문의사항이 있으시면 언제든 연락주세요.</p>
        </div>
        <div class="footer">
            <p>{{companyName}}</p>
            <p>이메일: info@falconcartons.com | 전화: 02-1234-5678</p>
        </div>
    </div>
</body>
</html>',
    1,
    '["customerName", "orderCode", "companyName", "orderDate"]',
    1,
    '주문 접수 시 고객에게 발송되는 확인 이메일',
    'system',
    'system',
    NOW(),
    NOW()
);

-- 2. 결제 안내 템플릿
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    '결제 안내 이메일',
    'PAYMENT_GUIDE',
    '[{{companyName}}] 결제 안내 (주문번호: {{orderCode}})',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>결제 안내</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #27ae60; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background-color: #f9f9f9; }
        .payment-info { background-color: white; padding: 15px; margin: 15px 0; border-radius: 5px; border-left: 4px solid #27ae60; }
        .notice { background-color: #fff3cd; padding: 15px; margin: 15px 0; border-radius: 5px; }
        .footer { text-align: center; padding: 20px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>{{companyName}}</h1>
            <p>결제 안내</p>
        </div>
        <div class="content">
            <p><strong>{{customerName}}</strong>님, 안녕하세요!</p>
            <p>주문하신 상품의 결제 방법을 안내해드립니다.</p>
            
            <div class="payment-info">
                <h3>결제 정보</h3>
                <p><strong>주문번호:</strong> {{orderCode}}</p>
                <p><strong>결제방식:</strong> {{paymentMethod}}</p>
            </div>
            
            <div class="notice">
                <h4>💰 현금 결제 안내</h4>
                <p>매장에서 직접 현금으로 결제해주시면 됩니다.</p>
                <p>방문 전에 미리 연락 주시면 상품을 준비해두겠습니다.</p>
            </div>
            
            <p><strong>매장 정보:</strong></p>
            <p>📍 주소: 서울시 강남구 테헤란로 123</p>
            <p>📞 전화: 02-1234-5678</p>
            <p>🕒 운영시간: 월~금 09:00-18:00</p>
        </div>
        <div class="footer">
            <p>{{companyName}}</p>
            <p>이메일: info@falconcartons.com</p>
        </div>
    </div>
</body>
</html>',
    1,
    '["customerName", "orderCode", "paymentMethod", "companyName"]',
    1,
    '현금/코인 결제 방식 안내 이메일',
    'system',
    'system',
    NOW(),
    NOW()
);

-- 3. 임시 비밀번호 템플릿
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    '임시 비밀번호 발송',
    'TEMP_PASSWORD',
    '[{{companyName}}] 임시 비밀번호 발송',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>임시 비밀번호 발송</title>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #e74c3c; color: white; padding: 20px; text-align: center; }
        .content { padding: 20px; background-color: #f9f9f9; }
        .password-box { background-color: white; padding: 20px; margin: 20px 0; border-radius: 5px; border: 2px solid #e74c3c; text-align: center; }
        .password { font-size: 24px; font-weight: bold; color: #e74c3c; letter-spacing: 2px; }
        .warning { background-color: #fff3cd; padding: 15px; margin: 15px 0; border-radius: 5px; }
        .footer { text-align: center; padding: 20px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔐 {{companyName}}</h1>
            <p>임시 비밀번호 발송</p>
        </div>
        <div class="content">
            <p><strong>{{username}}</strong>님, 안녕하세요!</p>
            <p>요청하신 임시 비밀번호를 발송해드립니다.</p>
            
            <div class="password-box">
                <p><strong>임시 비밀번호</strong></p>
                <div class="password">{{tempPassword}}</div>
            </div>
            
            <div class="warning">
                <p><strong>⚠️ 보안 안내</strong></p>
                <ul>
                    <li>로그인 후 <strong>반드시 비밀번호를 변경</strong>해주세요</li>
                    <li>임시 비밀번호는 타인에게 노출되지 않도록 주의하세요</li>
                    <li>비밀번호 변경은 마이페이지에서 가능합니다</li>
                </ul>
            </div>
        </div>
        <div class="footer">
            <p>{{companyName}}</p>
            <p>이메일: info@falconcartons.com</p>
        </div>
    </div>
</body>
</html>',
    1,
    '["username", "tempPassword", "companyName"]',
    1,
    '임시 비밀번호 발송 이메일',
    'system',
    'system',
    NOW(),
    NOW()
);
