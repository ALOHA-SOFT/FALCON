package com.falcon.shop.domain.users;

import java.util.List;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;
import com.falcon.shop.domain.products.Products;
import com.falcon.shop.domain.shop.CartItemOption;

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
@TableName("carts")
@Alias("Carts")
public class Carts extends Base {
  private Long userNo;
  private Long productNo;
  private Long quantity;
  private Double totalPrice;
  private String options; 
  /* options - JSON
    [
      {"option_no": 1, "quantity": 2, "price": 20000},
      {"option_no": 2, "quantity": 1, "price": 10000}
    ]
  */

  @TableField(exist = false)
  private Products product; // 장바구니에 담긴 상품 정보
  @TableField(exist = false)
  private List<CartItemOption> cartItemOptions; // 장바구니에 담긴 옵션 정보


  /**
   * options (JSON 형태) 를 CartItemOption 객체 리스트로 변환
   */
  public void toItemOption() {
    if (this.options != null && !this.options.isEmpty()) {
      log.info("options : {}", this.options);
      // JSON 문자열을 CartItemOption 객체 리스트로 변환
      this.cartItemOptions = CartItemOption.fromJsonArray(this.options);
    } else {
      this.cartItemOptions = List.of(); // 빈 리스트로 초기화
    }
  }
}
