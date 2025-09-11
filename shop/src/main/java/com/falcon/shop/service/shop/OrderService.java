package com.falcon.shop.service.shop;

import com.github.pagehelper.PageInfo;
import com.falcon.shop.domain.common.QueryParams;
import com.falcon.shop.domain.shop.Orders;
import com.falcon.shop.service.BaseService;

public interface OrderService extends BaseService<Orders> {
    
    // 주문 목록 조회
    public PageInfo<Orders> page(QueryParams queryParams);
    public PageInfo<Orders> page(QueryParams queryParams, Orders order);
    
    // 주문내역 (👩‍💼회원)
    public PageInfo<Orders> pageByUserNo(QueryParams queryParams, Long userNo);

    // 주문 생성
    public Orders createOrder(Orders order);

    // 주문 조회 - ID
    public Orders selectById(String id);

    // 주문처리
    public boolean processOrder(Orders order);

    
}
