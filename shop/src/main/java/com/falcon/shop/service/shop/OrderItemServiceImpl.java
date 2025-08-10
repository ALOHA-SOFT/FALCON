package com.falcon.shop.service.shop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.falcon.shop.domain.shop.OrderItem;
import com.falcon.shop.mapper.shop.OrderItemMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItem, OrderItemMapper> implements OrderItemService {

    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Override
    public void insertOrderItem(OrderItem orderItem) {
        orderItemMapper.insert(orderItem);
    }
    
    @Override
    public void insertOrderItems(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            orderItemMapper.insert(orderItem);
        }
    }
    
    @Override
    public List<OrderItem> getOrderItemsByOrderNo(Long orderNo) {
        return orderItemMapper.getOrderItemsByOrderNo(orderNo);
    }
    
    @Override
    public OrderItem getOrderItemByNo(Long no) {
        return orderItemMapper.selectById(no);
    }
}
