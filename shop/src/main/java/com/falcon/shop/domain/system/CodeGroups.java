package com.falcon.shop.domain.system;

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
@TableName("code_groups")    // 테이블명 (Mybatis plus)
@Alias("CodeGroups")        // 별칭 (Mybatis package 생략용 - Mapper에서 사용)
public class CodeGroups extends Base {

    private String name;        // 코드그룹명
    private String description; // 설명

}
