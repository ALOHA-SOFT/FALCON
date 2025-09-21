package com.falcon.shop.domain.shop;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;
import com.falcon.shop.domain.products.Products;

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
@TableName("order_item")
@Alias("OrderItem")
public class OrderItem extends Base {
  
  @TableId(type = IdType.AUTO)        // PK 자동증가 (Mybatis plus - insert 에서 사용)
  private Long no;                    // PK

  private Long productNo;
  private Long orderNo;
  private Long quantity;
  private BigDecimal price;

  @TableField(exist = false)
  private Products product;
  
  @TableField(exist = false)
  private List<OrderItemOption> orderItemOptions;
}
