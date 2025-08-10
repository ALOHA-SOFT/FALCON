package com.falcon.shop.service.shop;

import java.util.List;

import com.falcon.shop.domain.shop.OrderItem;
import com.falcon.shop.service.BaseService;

public interface OrderItemService extends BaseService<OrderItem> {
    
    // 주문 항목 저장
    void insertOrderItem(OrderItem orderItem);
    
    // 주문 항목 여러 개 저장
    void insertOrderItems(List<OrderItem> orderItems);
    
    // 주문 번호로 주문 항목 조회
    List<OrderItem> getOrderItemsByOrderNo(Long orderNo);
    
    // 주문 항목 번호로 조회
    OrderItem getOrderItemByNo(Long no);
}
