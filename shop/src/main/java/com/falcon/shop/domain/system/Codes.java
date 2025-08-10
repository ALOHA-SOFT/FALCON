package com.falcon.shop.domain.system;

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
@TableName("codes")    // 테이블명 (Mybatis plus)
@Alias("Codes")        // 별칭 (Mybatis package 생략용 - Mapper에서 사용)
public class Codes extends Base {

    private Long codeGroupNo;   // FK (code_groups 테이블의 no)
    private String name;        // 코드명
    private String value;       // 코드 값
    private String code;        // 업무코드
    private String description; // 설명
    private Integer seq;        // 순서
    @TableField(exist = false)
    private CodeGroups codeGroups; // 코드 그룹 정보 (FK 조인용)

}
