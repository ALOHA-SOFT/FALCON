package com.falcon.shop.domain.shop;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("payments")
@Alias("Payments")
public class Payments extends Base {
  private Long orderNo;
  private String method;
  private String status;    // 상태 ('결제대기','결제완료','결제실패')
  private String paymentKey;
  private Long amount; // 결제금액


  
}
