package com.falcon.shop.domain.shop;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("cancellations")
@Alias("Cancellations")
public class Cancellations extends Base {
  private Long orderNo;
  private String type;    // 주문 취소, 반품 등
  private String reason;
  private Boolean isConfirmed;
  private Boolean isRefund;
  private String accountNumber;
  private String bankName;
  private String depositor;
  private String status; // 상태 (예: '취소요청', '취소완료')

  @TableField(exist = false)
  private Orders order; // 주문 정보 (조인된 결과)
}
