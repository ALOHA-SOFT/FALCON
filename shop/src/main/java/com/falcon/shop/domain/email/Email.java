package com.falcon.shop.domain.email;

import java.time.LocalDateTime;

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
@TableName("emails")
@Alias("Email")
public class Email extends Base {

    @TableId(type = IdType.AUTO)
    private Long no;                    // PK
    
    private Long templateNo;            // FK (이메일 템플릿)
    private String recipientEmail;      // 받는사람 이메일
    private String recipientName;       // 받는사람 이름
    private String senderEmail;         // 보내는사람 이메일
    private String senderName;          // 보내는사람 이름
    private String subject;             // 제목
    private String content;             // 내용
    private Boolean isHtml;             // HTML 여부
    private SendStatus sendStatus;      // 발송상태
    private String sendType;            // 발송타입
    private String relatedId;           // 관련 ID
    private LocalDateTime sentAt;       // 발송일시
    private String errorMessage;        // 오류메시지
    @Builder.Default
    private Integer retryCount = 0;     // 재시도 횟수

    public enum SendStatus {
        PENDING,    // 대기중
        SENT,       // 발송완료
        FAILED      // 발송실패
    }
    
    // 편의 메서드들
    public Boolean getIsHtml() {
        return isHtml != null ? isHtml : false;
    }
    
    public void setIsHtml(Boolean isHtml) {
        this.isHtml = isHtml;
    }
}
