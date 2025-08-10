package com.falcon.shop.domain.products;

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
@TableName("category")
@Alias("Category")
public class Category extends Base {
    private String name;    // 카테고리명
    private String description; // 카테고리 설명
    private Integer seq; // 순서 (정렬용)

    @TableField(exist = false)
    private List<CategoryLarge> categoryLargeList; // 대분류 목록
}
