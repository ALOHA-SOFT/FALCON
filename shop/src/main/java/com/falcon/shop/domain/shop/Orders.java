package com.falcon.shop.domain.shop;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;
import com.falcon.shop.domain.users.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
@Alias("Orders")
public class Orders extends Base {

  @TableId(type = IdType.AUTO)                // PK 자동증가 (Mybatis plus - insert 에서 사용)
  private Long no;                            // PK
  
  private Long userNo;                       // FK
  private Long addressNo;                    // FK
  private Long shipmentNo;                   // FK
  private String code;                       // 주문코드 (20250101_상품번호_유저번호_당일시퀀스)
  private String title;                      // 주문제목 (상품1 외 5건)
  // private String guestTel;                   // 비회원 전화번호
  private String guestEmail;                 // 비회원 이메일
  private String guestFirstName;             // 비회원 성
  private String guestLastName;              // 비회원 이름
  private BigDecimal totalPrice;             // 총 가격
  private Long totalQuantity;                // 총 수량
  private Long totalItemCount;               // 총 항목수
  private BigDecimal shipPrice;              // 배송비
  private String paymentMethod;              // 결제방식 (TRANSFER, COIN)
  private String status;                     // 상태 ('결제대기','결제완료','배송준비중','배송시작','배송중','배송완료','주문취소','환불완료')
 
  // 주문 상품 아이템
  @TableField(exist = false)
  List<OrderItem> orderItems;

  // 주문자 정보
  @TableField(exist = false)
  private Users user;
  
  // 배송 정보
  @TableField(exist = false)
  private Shipments shipment;

  
}
