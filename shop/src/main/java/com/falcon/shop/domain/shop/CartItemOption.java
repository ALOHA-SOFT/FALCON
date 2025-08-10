package com.falcon.shop.domain.shop;

import java.util.List;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.falcon.shop.domain.Base;
import com.falcon.shop.domain.products.Options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("cart_item_option")
@Alias("CartItemOption")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CartItemOption extends Base {

    @TableId(type = IdType.AUTO)        // PK 자동증가 (Mybatis plus - insert 에서 사용)
    private Long no;                    // PK
    
    private Long orderItemNo;       // FK: 주문항목
    private Long optionNo;          // FK: 선택한 옵션
    private Long quantity;          // 해당 옵션 수량
    private Long price;             // 해당 옵션 가격 (snapshot)

    @TableField(exist = false)
    private Options option;

    public static List<CartItemOption> fromJsonArray(String options) {
        log.info("options : {}", options);
        if (options == null || options.isEmpty()) {
            return List.of(); // 빈 리스트 반환
        }
        // JSON 배열 파싱 로직 추가
        // 예시로 Jackson 라이브러리를 사용하여 JSON 배열을 List로 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(options, new TypeReference<List<CartItemOption>>() {});
        } catch (Exception e) {
            log.error("Error parsing options JSON: {}", e.getMessage());
            return List.of(); // 오류 발생 시 빈 리스트 반환
        }
    }

    
}
