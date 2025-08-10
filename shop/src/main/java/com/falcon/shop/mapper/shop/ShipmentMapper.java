package com.falcon.shop.mapper.shop;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.shop.Shipments;

@Mapper
public interface ShipmentMapper extends BaseMapper<Shipments> {

}
