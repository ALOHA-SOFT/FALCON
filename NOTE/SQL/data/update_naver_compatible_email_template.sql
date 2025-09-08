-- Active: 1754231727979@@falcon-db.cr8aiiek0cvi.eu-west-2.rds.amazonaws.com@3306@falcon
-- 네이버 호환 결제 안내 이메일 템플릿 업데이트
UPDATE email_templates 
SET content = '<table cellpadding="0" cellspacing="0" border="0" width="100%" style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
  <tr>
    <td align="center" style="padding: 0; background-color: #f4f4f4;">
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="background-color: #f4f4f4; border: 1px solid #ddd;">
        <!-- 헤더 -->
        <tr>
          <td style="background-color: #27ae60; padding: 30px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">{{companyName}}</h1>
            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">Order Request Form Submitted Successfully</p>
          </td>
        </tr>
        <!-- 메인 콘텐츠 -->
        <tr>
          <td style="padding: 30px; background-color: #f9f9f9;">
            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;"><strong>{{customerName}}</strong></p>
            
            <!-- 결제 정보 박스 -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #ffffff; border: 1px solid #27ae60; margin: 20px 0;">
              <tr>
                <td style="padding: 20px; border-left: 4px solid #27ae60;">
                  <h3 style="margin: 0 0 15px 0; color: #27ae60; font-size: 18px;">Order Informantion</h3>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>Order No.:</strong> {{orderCode}}</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>Payment Method:</strong> {{paymentMethod}}</p>
                </td>
              </tr>
            </table>
            
            <!-- 현금 결제 안내 박스 -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #fff3cd; border: 1px solid #ffeaa7; margin: 20px 0;">
              <tr>
                <td style="padding: 20px;">
                  <h4 style="margin: 0 0 10px 0; color: #d35400; font-size: 16px;">💰 Payment Instructions</h4>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">We will send you a separate payment instruction via email in the next 24 hours.</p>
                </td>
              </tr>
            </table>
            
          </td>
        </tr>
        <!-- 푸터 -->
        <tr>
          <td style="background-color: #333333; padding: 20px; text-align: center;">
            <p style="margin: 0 0 5px 0; font-size: 16px; color: #ffffff; font-weight: bold;">{{companyName}}</p>
            <p style="margin: 0; font-size: 14px; color: #cccccc;"> info@falconcartons.com</p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>'
WHERE type = 'PAYMENT_GUIDE';

-- 네이버 호환 주문 확인 이메일 템플릿 업데이트
UPDATE email_templates 
SET content = '<table cellpadding="0" cellspacing="0" border="0" width="100%" style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
  <tr>
    <td align="center" style="padding: 20px;">
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="background-color: #ffffff; border: 1px solid #ddd;">
        <!-- 헤더 -->
        <tr>
          <td style="background-color: #2c3e50; padding: 30px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">{{companyName}}</h1>
            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">주문 확인서</p>
          </td>
        </tr>
        <!-- 메인 콘텐츠 -->
        <tr>
          <td style="padding: 30px; background-color: #f9f9f9;">
            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;"><strong>{{customerName}}</strong>님, 안녕하세요!</p>
            <p style="margin: 0 0 30px 0; font-size: 14px; color: #333; line-height: 1.6;">주문해주셔서 감사합니다. 주문 내용을 확인해주세요.</p>
            
            <!-- 주문 정보 박스 -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #ffffff; border: 1px solid #2c3e50; margin: 20px 0;">
              <tr>
                <td style="padding: 20px; border-left: 4px solid #2c3e50;">
                  <h3 style="margin: 0 0 15px 0; color: #2c3e50; font-size: 18px;">주문 정보</h3>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>주문번호:</strong> {{orderCode}}</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>주문일시:</strong> {{orderDate}}</p>
                </td>
              </tr>
            </table>
            
            <!-- 안내 메시지 -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #e8f5e8; border: 1px solid #27ae60; margin: 20px 0;">
              <tr>
                <td style="padding: 20px;">
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">주문하신 상품을 정성껏 준비하여 배송해드리겠습니다.</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">배송 관련 문의사항이 있으시면 언제든 연락주세요.</p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <!-- 푸터 -->
        <tr>
          <td style="background-color: #333333; padding: 20px; text-align: center;">
            <p style="margin: 0 0 5px 0; font-size: 16px; color: #ffffff; font-weight: bold;">{{companyName}}</p>
            <p style="margin: 0; font-size: 14px; color: #cccccc;">이메일: info@falconcartons.com | 전화: 02-1234-5678</p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>'
WHERE type = 'ORDER_CONFIRMATION';

-- 네이버 호환 임시 비밀번호 이메일 템플릿 업데이트
UPDATE email_templates 
SET content = '<table cellpadding="0" cellspacing="0" border="0" width="100%" style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
  <tr>
    <td align="center" style="padding: 20px;">
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="background-color: #ffffff; border: 1px solid #ddd;">
        <!-- 헤더 -->
        <tr>
          <td style="background-color: #e74c3c; padding: 30px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">🔐 {{companyName}}</h1>
            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">임시 비밀번호 발송</p>
          </td>
        </tr>
        <!-- 메인 콘텐츠 -->
        <tr>
          <td style="padding: 30px; background-color: #f9f9f9;">
            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;"><strong>{{username}}</strong>님, 안녕하세요!</p>
            <p style="margin: 0 0 30px 0; font-size: 14px; color: #333; line-height: 1.6;">요청하신 임시 비밀번호를 발송해드립니다.</p>
            
            <!-- 임시 비밀번호 박스 -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #ffffff; border: 2px solid #e74c3c; margin: 20px 0;">
              <tr>
                <td style="padding: 30px; text-align: center;">
                  <p style="margin: 0 0 15px 0; color: #333; font-size: 16px; font-weight: bold;">임시 비밀번호</p>
                  <p style="margin: 0; font-size: 28px; font-weight: bold; color: #e74c3c; letter-spacing: 3px;">{{tempPassword}}</p>
                </td>
              </tr>
            </table>
            
            <!-- 보안 안내 박스 -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #fff3cd; border: 1px solid #ffeaa7; margin: 20px 0;">
              <tr>
                <td style="padding: 20px;">
                  <p style="margin: 0 0 15px 0; color: #d35400; font-size: 16px; font-weight: bold;">⚠️ 보안 안내</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">• 로그인 후 <strong>반드시 비밀번호를 변경</strong>해주세요</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">• 임시 비밀번호는 타인에게 노출되지 않도록 주의하세요</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">• 비밀번호 변경은 마이페이지에서 가능합니다</p>
                </td>
              </tr>
            </table>
          </td>
        </tr>
        <!-- 푸터 -->
        <tr>
          <td style="background-color: #333333; padding: 20px; text-align: center;">
            <p style="margin: 0 0 5px 0; font-size: 16px; color: #ffffff; font-weight: bold;">{{companyName}}</p>
            <p style="margin: 0; font-size: 14px; color: #cccccc;">이메일: info@falconcartons.com | 전화: 02-1234-5678</p>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>'
WHERE type = 'TEMP_PASSWORD';

-- 업데이트 확인
SELECT name, type, is_html FROM email_templates WHERE type IN ('PAYMENT_GUIDE', 'ORDER_CONFIRMATION', 'TEMP_PASSWORD');


