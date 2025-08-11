package com.falcon.shop.domain.email;

import org.apache.ibatis.type.Alias;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.falcon.shop.domain.Base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("email_templates")
@Alias("EmailTemplate")
public class EmailTemplate extends Base {

    @TableId(type = IdType.AUTO)
    private Long no;                    // PK
    
    private String name;                // 템플릿명
    private String type;                // 템플릿 타입
    private String subject;             // 제목 템플릿
    private String content;             // 내용 템플릿
    private Boolean isHtml;             // HTML 여부
    private String variables;           // 사용 가능한 변수 (JSON)
    @Builder.Default
    private Boolean isActive = true;    // 활성화 여부
    private String description;         // 설명
    
    // 편의 메서드들
    public Boolean getIsHtml() {
        return isHtml != null ? isHtml : false;
    }
    
    public void setIsHtml(Boolean isHtml) {
        this.isHtml = isHtml;
    }
}
