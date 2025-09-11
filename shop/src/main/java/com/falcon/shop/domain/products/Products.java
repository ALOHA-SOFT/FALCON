package com.falcon.shop.domain.products;

import java.math.BigDecimal;
import java.util.List;

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
@TableName("products")
@Alias("Products")
public class Products extends Base {

  private Long no;                   // 상품번호 (PK)
  private String name;                // 상품명
  private Long stock;                 // 재고
  private Long categoryNo;            // 카테고리 번호 (FK)
  private Long categoryLargeNo;       // 대분류 번호 (FK)
  private Long optionGroupNo;         // 옵션그룹 번호 (FK, nullable)
  private BigDecimal price;                 // 가격
  private BigDecimal shipPrice;             // 배송비
  private String shipMsg;             // 배송안내
  private String summary;             // 상품정보 요약
  private String content;             // 상품상세
  private Boolean isNew;              // NEW
  private Boolean isBest;             // BEST
  private Boolean isSoldOut;          // 품절
  private String priceInfo;           // 가격설명


  @TableField(exist = false)
  private String imageUrl;
  
  // 연관 객체들 (조회용)
  @TableField(exist = false)
  private Category category;          // 카테고리 정보
  
  @TableField(exist = false)
  private CategoryLarge categoryLarge; // 대분류 정보

  /* ################## 미디어 정보 ################## */
  @TableField(exist = false) private List<Media> mediaList;          // 상품 미디어 정보
  @TableField(exist = false) private List<Media> imgList;            // 일반 이미지 정보
  @TableField(exist = false) private Media thumbImg;           // 썸네일 이미지           
  @TableField(exist = false) private Media mainImg;            // 메인 이미지
  @TableField(exist = false) private Media thumbVideo;        // 썸네일 동영상
  @TableField(exist = false) private Media mainVideo;         // 메인 동영상
}
