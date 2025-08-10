package com.falcon.shop.domain.system;

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
@TableName("seq_groups")    // 테이블명 (Mybatis plus)
@Alias("SeqGroups")        // 별칭 (Mybatis package 생략용 - Mapper에서 사용)
public class SeqGroups extends Base {

    private String code;        // 시퀀스를 식별하는 코드
    private String name;        // 시퀀스 이름
    private Long value;         // 그룹 누적 시퀀스 번호
    private Long step;          // 증감치 (기본적으로 +1)
    private String description; // 설명
    @TableField(exist = false)
    private List<Seq> seqList; // 시퀀스 목록 (연관된 Seq 객체들)
}
