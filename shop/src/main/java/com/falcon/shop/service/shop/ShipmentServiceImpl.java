package com.falcon.shop.service.shop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.falcon.shop.domain.shop.Shipments;
import com.falcon.shop.mapper.shop.ShipmentMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShipmentServiceImpl extends BaseServiceImpl<Shipments, ShipmentMapper> implements ShipmentService {

  @Autowired private ShipmentMapper shipmentMapper;

  @Override
  public List<Shipments> listByUser(Long userNo) {
    QueryWrapper<Shipments> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("user_no", userNo);
    queryWrapper.orderByDesc("created_at");
    List<Shipments> shipments = shipmentMapper.selectList(queryWrapper);
    log.info("Retrieved {} shipments for user {}", shipments.size(), userNo);
    return shipments;
  }

  
  
}
