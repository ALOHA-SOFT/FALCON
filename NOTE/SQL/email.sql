-- Active: 1754738700814@@127.0.0.1@3306@falcon
USE `falcon`;

SET FOREIGN_KEY_CHECKS = 0;

-- 이메일 발송 내역 테이블
DROP TABLE IF EXISTS `emails`;

CREATE TABLE `emails` (
	`no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`id` VARCHAR(64) NOT NULL UNIQUE COMMENT 'UK',
	`template_no` BIGINT NULL COMMENT 'FK (이메일 템플릿)',
	`recipient_email` VARCHAR(255) NOT NULL COMMENT '받는사람 이메일',
	`recipient_name` VARCHAR(100) NULL COMMENT '받는사람 이름',
	`sender_email` VARCHAR(255) NOT NULL COMMENT '보내는사람 이메일',
	`sender_name` VARCHAR(100) NULL COMMENT '보내는사람 이름',
	`subject` VARCHAR(500) NOT NULL COMMENT '제목',
	`content` TEXT NOT NULL COMMENT '내용',
	`html_content` TEXT NULL COMMENT 'HTML 내용',
	`send_status` ENUM('PENDING','SENT','FAILED') NOT NULL DEFAULT 'PENDING' COMMENT '발송상태 (대기중, 발송완료, 발송실패)',
	`send_type` VARCHAR(50) NULL COMMENT '발송타입 (ORDER, PAYMENT, MANUAL 등)',
	`related_id` VARCHAR(64) NULL COMMENT '관련 ID (주문번호, 결제번호 등)',
	`send_at` TIMESTAMP NULL COMMENT '발송일시',
	`error_message` TEXT NULL COMMENT '오류메시지',
	`retry_count` INT NOT NULL DEFAULT 0 COMMENT '재시도 횟수',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
	PRIMARY KEY (`no`),
	INDEX `idx_recipient_email` (`recipient_email`),
	INDEX `idx_send_status` (`send_status`),
	INDEX `idx_send_type` (`send_type`),
	INDEX `idx_related_id` (`related_id`),
	INDEX `idx_send_at` (`send_at`)
);

-- 이메일 템플릿 테이블
DROP TABLE IF EXISTS `email_templates`;

CREATE TABLE `email_templates` (
	`no` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PK',
	`id` VARCHAR(64) NOT NULL UNIQUE COMMENT 'UK',
	`name` VARCHAR(100) NOT NULL COMMENT '템플릿명',
	`type` VARCHAR(50) NOT NULL COMMENT '템플릿 타입 (ORDER, PAYMENT, MANUAL 등)',
	`subject` VARCHAR(500) NOT NULL COMMENT '제목 템플릿',
	`content` TEXT NOT NULL COMMENT '내용 템플릿',
	`is_html` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'HTML 여부',
	`html_content` TEXT NULL COMMENT 'HTML 내용 템플릿',
	`variables` TEXT NULL COMMENT '사용 가능한 변수 (JSON)',
	`is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성화 여부',
	`description` TEXT NULL COMMENT '설명',
	`created_by` VARCHAR(64) NULL COMMENT '등록자',
	`updated_by` VARCHAR(64) NULL COMMENT '수정자',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
	PRIMARY KEY (`no`),
	INDEX `idx_type` (`type`),
	INDEX `idx_is_active` (`is_active`)
);

-- 외래키 제약조건 추가
ALTER TABLE `emails` ADD CONSTRAINT `fk_emails_template` 
FOREIGN KEY (`template_no`) REFERENCES `email_templates` (`no`) ON DELETE SET NULL ON UPDATE CASCADE;


SET FOREIGN_KEY_CHECKS = 1;
