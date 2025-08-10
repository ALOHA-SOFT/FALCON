package com.falcon.shop.domain.products;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("options")
@Alias("Options")
public class Options extends Base {
  
  @TableId(type = IdType.AUTO)
  private Long no; // PK
  private Long groupNo;
  private String name;
  private Long price;
  private Long stock;

  @TableField(exist = false)
  private OptionGroup optionGroup; // 연관된 옵션 그룹 정보
}
