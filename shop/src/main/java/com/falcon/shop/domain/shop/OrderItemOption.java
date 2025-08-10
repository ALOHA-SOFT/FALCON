package com.falcon.shop.domain.shop;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;
import com.falcon.shop.domain.products.Options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("order_item_option")
@Alias("OrderItemOption")
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderItemOption extends Base {

    @TableId(type = IdType.AUTO)        // PK 자동증가 (Mybatis plus - insert 에서 사용)
    private Long no;                    // PK
    
    private Long orderItemNo;       // FK: 주문항목
    private Long optionNo;          // FK: 선택한 옵션
    private Long quantity;          // 해당 옵션 수량
    private Long price;             // 해당 옵션 가격 (snapshot)

    @TableField(exist = false)
    private Options option;

    
}
