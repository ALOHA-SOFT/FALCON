package com.falcon.shop.domain.users;

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
@TableName("address")
@Alias("Address")
public class Address extends Base {
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
}
