package com.falcon.shop.domain.system;

import java.util.Date;

import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

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
@TableName("seq")    // 테이블명 (Mybatis plus)
@Alias("Seq")        // 별칭 (Mybatis package 생략용 - Mapper에서 사용)
public class Seq extends Base {

    private Long seqGroupNo;    // FK (seq_groups 테이블의 no)
    private String code;        // 시퀀스를 식별하는 코드 (seq_group_code)
    private Long value;         // 현재 시퀀스 번호
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @TableField(exist = false)
    private SeqGroups seqGroups; // 시퀀스 그룹 정보 (FK로 연결된 seq_groups 테이블의 정보)

}
