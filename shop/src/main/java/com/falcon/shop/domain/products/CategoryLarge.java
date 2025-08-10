package com.falcon.shop.domain.products;

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
@TableName("category_large")
@Alias("CategoryLarge")
public class CategoryLarge extends Base {
    private Long categoryNo;    // 카테고리 번호 (FK)
    private String name;        // 대분류명
    private Integer seq;        // 순서 (정렬용)
    
    @TableField(exist = false)
    private Category category;  // 연관된 카테고리 정보
}
