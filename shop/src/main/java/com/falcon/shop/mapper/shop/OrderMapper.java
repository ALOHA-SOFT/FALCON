package com.falcon.shop.mapper.shop;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.shop.Orders;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

    public List<Orders> listWithParams(Map<String, Object> params);
    
    public Orders selectById(String id);

}
