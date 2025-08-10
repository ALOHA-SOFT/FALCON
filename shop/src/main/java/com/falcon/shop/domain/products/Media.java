package com.falcon.shop.domain.products;

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
@TableName("media")
@Alias("Media")
public class Media extends Base {
  private Long productNo;      // FK
  private Boolean isMain;      // 메인미디어
  private Boolean isThumb;     // 썸네일
  private Integer thumbSeq;    // 썸네일순서
  private String type;         // 타입 ('이미지','동영상','임베드')
  private String content;      // 컨텐츠( URL, 임베드 코드 등 )
}
