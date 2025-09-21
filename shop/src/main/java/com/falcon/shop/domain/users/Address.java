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
  // private String tel;
  private String recipient;
  private String address;     // 주소
  private String city;        // 도시
  private String postcode;    // 우편번호
  private String country;     // 국가
  private Boolean isMain;
  private String deliveryRequest;
  private String deliveryMethod;
}
