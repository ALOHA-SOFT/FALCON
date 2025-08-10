package com.falcon.shop.mapper.shop;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.shop.OrderItem;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    // 주문 번호로 주문 항목 조회
    List<OrderItem> getOrderItemsByOrderNo(@Param("orderNo") Long orderNo);

}
