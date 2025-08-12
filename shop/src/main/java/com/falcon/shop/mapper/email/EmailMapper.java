package com.falcon.shop.mapper.email;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.falcon.shop.domain.email.Email;

@Mapper
public interface EmailMapper extends BaseMapper<Email> {
    
    /**
     * 발송 상태별 이메일 목록 조회
     * @param sendStatus 발송 상태
     * @return 이메일 목록
     */
    List<Email> selectBySendStatus(@Param("sendStatus") String sendStatus);
    
    /**
     * 발송 타입별 이메일 목록 조회
     * @param sendType 발송 타입
     * @return 이메일 목록
     */
    List<Email> selectBySendType(@Param("sendType") String sendType);
    
    /**
     * 관련 ID로 이메일 목록 조회
     * @param relatedId 관련 ID
     * @return 이메일 목록
     */
    List<Email> selectByRelatedId(@Param("relatedId") String relatedId);
    
    /**
     * 수신자 이메일로 이메일 목록 조회
     * @param recipientEmail 수신자 이메일
     * @return 이메일 목록
     */
    List<Email> selectByRecipientEmail(@Param("recipientEmail") String recipientEmail);
}
