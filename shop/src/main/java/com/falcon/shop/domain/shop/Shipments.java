package com.falcon.shop.domain.shop;

import java.util.UUID;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@TableName("shipments")
@Alias("Shipments")
public class Shipments extends Base {

  @TableId(type = IdType.AUTO)
  private Long no;                // PK
  private String id;                          // ID
  private Long userNo;
  private String recipient;
  private String address;         // 주소
  private String city;            // 도시
  private String postcode;        // 우편번호
  private String country;         // 국가/지역
  private Boolean isMain;
  private String trackingNo;      // 운송장 번호
  private String deliveryMethod;  // 배송방법 (택배, 퀵, 방문수령 등)
  private String shipCompany;     // 택배 회사
  private String status;          // 배송상태

  public Shipments() {
    this.id = UUID.randomUUID().toString();         // UUID로 ID 생성
  }

  // public enum Status {
  //   배송준비중, 배송시작, 배송중, 배송완료, 주문취소
  // }

  


}
