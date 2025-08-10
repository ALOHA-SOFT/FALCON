package com.falcon.shop.service.shop;

import com.falcon.shop.domain.shop.Payments;
import com.falcon.shop.service.BaseService;

public interface PaymentService extends BaseService<Payments> {
    
    // 주문번호로 결제 정보 조회
    Payments selectByOrderNo(Long orderNo);
    
}
