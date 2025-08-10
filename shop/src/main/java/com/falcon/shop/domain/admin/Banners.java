package com.falcon.shop.domain.admin;

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
@TableName("banners")
@Alias("Banners")
public class Banners extends Base {
  private String type;
  private String name;
  private String url;
  private String link; 
  private Integer seq; 
  private String mainTitle;
  private String subTitle;
}
