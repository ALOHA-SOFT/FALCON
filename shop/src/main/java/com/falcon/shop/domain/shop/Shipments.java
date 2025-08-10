package com.falcon.shop.domain.shop;

import org.apache.ibatis.type.Alias;

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
@TableName("shipments")
@Alias("Shipments")
public class Shipments extends Base {
  private Long userNo;
  private String tel;
  private String recipient;
  private String postcode; // 우편번호
  private String address;
  private String addressDong;
  private String addressDetail;
  private Boolean isMain;
  private String deliveryRequest;
  private String deliveryMethod;
  private String trackingNo;
  private String shipCompany;
  private Status status;

  public enum Status {
    배송준비중, 배송시작, 배송중, 배송완료, 주문취소
  }
}
