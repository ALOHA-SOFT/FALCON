package com.falcon.shop.service.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.falcon.shop.domain.shop.OrderItemOption;
import com.falcon.shop.mapper.shop.OrderItemOptionMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderItemOptionServiceImpl extends BaseServiceImpl<OrderItemOption, OrderItemOptionMapper> implements OrderItemOptionService {

    @Autowired
    private OrderItemOptionMapper orderItemOptionMapper;
    
}
