package com.falcon.shop.service.shop;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.falcon.shop.domain.shop.Payments;
import com.falcon.shop.mapper.shop.PaymentMapper;
import com.falcon.shop.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PaymentServiceImpl extends BaseServiceImpl<Payments, PaymentMapper> implements PaymentService {

  @Autowired private PaymentMapper paymentMapper;

  @Override
  public Payments selectByOrderNo(Long orderNo) {
      log.info("주문번호로 결제 정보 조회: {}", orderNo);
      QueryWrapper<Payments> queryWrapper = new QueryWrapper<>();
      queryWrapper.eq("order_no", orderNo);
      List<Payments> paymentList = paymentMapper.selectList(queryWrapper);
      if (paymentList != null && !paymentList.isEmpty()) {
          return paymentList.get(0); // 첫번째 결제 정보 반환
      }
      return null;
  }
  
}
