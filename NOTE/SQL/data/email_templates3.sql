-- Naver Compatible Email Templates Integrated Data Insert (Table Style)
-- Delete existing templates and insert new templates

-- Delete existing templates
DELETE FROM email_templates WHERE type IN ('PAYMENT_GUIDE', 'ORDER_CONFIRMATION', 'TEMP_PASSWORD');

-- 1. Naver Compatible Payment Guide Template
INSERT INTO email_templates (
    id, name, type, subject, content, is_html, variables, is_active, description,
    created_by, updated_by, created_at, updated_at
) VALUES (
    UUID(),
    'Payment Guide Email (Naver Compatible)',
    'PAYMENT_GUIDE',
    '[{{companyName}}] Payment Guide (Order No: {{orderCode}})',
    '<table cellpadding="0" cellspacing="0" border="0" width="100%" style="font-family: Arial, sans-serif; background-color: #f4f4f4;">
  <tr>
    <td align="center" style="padding: 20px;">
      <table cellpadding="0" cellspacing="0" border="0" width="600" style="background-color: #ffffff; border: 1px solid #ddd;">
        <!-- Header -->
        <tr>
          <td style="background-color: #27ae60; padding: 30px; text-align: center;">
            <h1 style="color: #ffffff; margin: 0; font-size: 28px; font-weight: bold;">{{companyName}}</h1>
            <p style="color: #ffffff; margin: 10px 0 0 0; font-size: 16px;">Payment Guide</p>
          </td>
        </tr>
        <!-- Main Content -->
        <tr>
          <td style="padding: 30px; background-color: #f9f9f9;">
            <p style="margin: 0 0 20px 0; font-size: 16px; color: #333;">Hello <strong>{{customerName}}</strong>!</p>
            <p style="margin: 0 0 30px 0; font-size: 14px; color: #333; line-height: 1.6;">We are providing payment method information for your order.</p>
            
            <!-- Payment Information Box -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #ffffff; border: 1px solid #27ae60; margin: 20px 0;">
              <tr>
                <td style="padding: 20px; border-left: 4px solid #27ae60;">
                  <h3 style="margin: 0 0 15px 0; color: #27ae60; font-size: 18px;">Payment Information</h3>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>Order Number:</strong> {{orderCode}}</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;">
                    <strong>Order Info Link:</strong> 
                    <a href="{{host}}/my/orders/{{orderId}}/{{userId}}">Order Info Link</a>
                  </p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333;"><strong>Payment Method:</strong> {{paymentMethod}}</p>
                </td>
              </tr>
            </table>
            
            <!-- Cash Payment Guide Box -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="background-color: #fff3cd; border: 1px solid #ffeaa7; margin: 20px 0;">
              <tr>
                <td style="padding: 20px;">
                  <h4 style="margin: 0 0 10px 0; color: #d35400; font-size: 16px;">💰 Cash Payment Guide</h4>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">Please proceed with payment according to the above payment method, and our staff will process it within 24 hours to prepare and ship your products.</p>
                  <p style="margin: 5px 0; font-size: 14px; color: #333; line-height: 1.6;">Please contact us in advance so we can prepare your products.</p>              
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
    'Order Confirmation Email (Naver Compatible)',
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
    'Temporary Password Email (Naver Compatible)',
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

-- Insert confirmation
SELECT name, type, is_html, description FROM email_templates WHERE type IN ('PAYMENT_GUIDE', 'ORDER_CONFIRMATION', 'TEMP_PASSWORD');