-- Active: 1757907901171@@falcon-db.cr8aiiek0cvi.eu-west-2.rds.amazonaws.com@3306@falcon
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: falcon
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `address` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `user_no` bigint NOT NULL COMMENT 'FK',
  `tel` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '전화번호',
  `recipient` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '받는사람',
  `address` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주소',
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '도시',
  `postcode` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '우편번호',
  `country` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '국가/지역',
  `is_main` tinyint(1) DEFAULT '0' COMMENT '기본배송지여부',
  `delivery_request` text COLLATE utf8mb4_unicode_ci COMMENT '배송요청사항',
  `delivery_method` text COLLATE utf8mb4_unicode_ci COMMENT '수령방법',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `user_no` (`user_no`),
  CONSTRAINT `address_ibfk_1` FOREIGN KEY (`user_no`) REFERENCES `users` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `banners`
--

DROP TABLE IF EXISTS `banners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banners` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '메인' COMMENT '타입',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이름',
  `main_title` text COLLATE utf8mb4_unicode_ci COMMENT '메인제목',
  `sub_title` text COLLATE utf8mb4_unicode_ci COMMENT '서브제목',
  `url` text COLLATE utf8mb4_unicode_ci COMMENT '이미지경로',
  `link` text COLLATE utf8mb4_unicode_ci COMMENT '링크',
  `seq` int DEFAULT '0' COMMENT '순서',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cancellations`
--

DROP TABLE IF EXISTS `cancellations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cancellations` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `order_no` bigint NOT NULL COMMENT 'FK',
  `type` enum('주문취소','반품') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '주문취소' COMMENT '타입 (''주문취소'',''반품'')',
  `reason` text COLLATE utf8mb4_unicode_ci COMMENT '취소사유',
  `is_confirmed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '승인여부',
  `is_refund` tinyint(1) DEFAULT '0' COMMENT '환불처리여부',
  `account_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불계좌번호',
  `bank_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불계좌은행',
  `depositor` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '환불예금주',
  `status` enum('취소요청','취소완료','환불완료') COLLATE utf8mb4_unicode_ci DEFAULT '취소요청' COMMENT '상태 (''취소요청'',''취소완료'',''환불완료'')',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `order_no` (`order_no`),
  CONSTRAINT `cancellations_ibfk_1` FOREIGN KEY (`order_no`) REFERENCES `orders` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `user_no` bigint NOT NULL COMMENT 'FK',
  `product_no` bigint NOT NULL COMMENT 'FK',
  `quantity` bigint DEFAULT NULL COMMENT '수량',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '수량x가격',
  `options` text COLLATE utf8mb4_unicode_ci COMMENT '옵션들(JSON)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `user_no` (`user_no`),
  KEY `product_no` (`product_no`),
  CONSTRAINT `carts_ibfk_1` FOREIGN KEY (`user_no`) REFERENCES `users` (`no`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `carts_ibfk_2` FOREIGN KEY (`product_no`) REFERENCES `products` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '카테고리명',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '카테고리설명',
  `seq` int NOT NULL DEFAULT '0' COMMENT '순서',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `category_large`
--

DROP TABLE IF EXISTS `category_large`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category_large` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `category_no` bigint NOT NULL COMMENT 'FK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '대분류',
  `seq` int NOT NULL DEFAULT '0' COMMENT '순서',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  KEY `category_no` (`category_no`),
  CONSTRAINT `category_large_ibfk_1` FOREIGN KEY (`category_no`) REFERENCES `category` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `code_groups`
--

DROP TABLE IF EXISTS `code_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `code_groups` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코드그룸명',
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '설명',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `codes`
--

DROP TABLE IF EXISTS `codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `codes` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `code_group_no` bigint NOT NULL COMMENT 'FK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코드명',
  `value` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '코드 값',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '업무코드',
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '설명',
  `seq` int NOT NULL DEFAULT '0' COMMENT '순서',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `code_group_no` (`code_group_no`),
  CONSTRAINT `codes_ibfk_1` FOREIGN KEY (`code_group_no`) REFERENCES `code_groups` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `email_templates`
--

DROP TABLE IF EXISTS `email_templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_templates` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '템플릿명',
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '템플릿 타입 (ORDER, PAYMENT, MANUAL 등)',
  `subject` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '제목 템플릿',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '내용 템플릿',
  `is_html` tinyint(1) NOT NULL DEFAULT '1' COMMENT 'HTML 여부',
  `html_content` text COLLATE utf8mb4_unicode_ci COMMENT 'HTML 내용 템플릿',
  `variables` text COLLATE utf8mb4_unicode_ci COMMENT '사용 가능한 변수 (JSON)',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성화 여부',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '설명',
  `created_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '등록자',
  `updated_by` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '수정자',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `emails`
--

DROP TABLE IF EXISTS `emails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `emails` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `template_no` bigint DEFAULT NULL COMMENT 'FK (이메일 템플릿)',
  `recipient_email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '받는사람 이메일',
  `recipient_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '받는사람 이름',
  `sender_email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '보내는사람 이메일',
  `sender_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '보내는사람 이름',
  `subject` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '제목',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '내용',
  `html_content` text COLLATE utf8mb4_unicode_ci COMMENT 'HTML 내용',
  `send_status` enum('PENDING','SENT','FAILED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '발송상태 (대기중, 발송완료, 발송실패)',
  `send_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '발송타입 (ORDER, PAYMENT, MANUAL 등)',
  `related_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '관련 ID (주문번호, 결제번호 등)',
  `send_at` timestamp NULL DEFAULT NULL COMMENT '발송일시',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '오류메시지',
  `retry_count` int NOT NULL DEFAULT '0' COMMENT '재시도 횟수',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `idx_recipient_email` (`recipient_email`),
  KEY `idx_send_status` (`send_status`),
  KEY `idx_send_type` (`send_type`),
  KEY `idx_related_id` (`related_id`),
  KEY `idx_send_at` (`send_at`),
  KEY `fk_emails_template` (`template_no`),
  CONSTRAINT `fk_emails_template` FOREIGN KEY (`template_no`) REFERENCES `email_templates` (`no`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `media`
--

DROP TABLE IF EXISTS `media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `media` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `product_no` bigint NOT NULL COMMENT 'FK',
  `is_main` tinyint(1) DEFAULT NULL COMMENT '메인미디어',
  `is_thumb` tinyint(1) DEFAULT NULL COMMENT '썸네일',
  `thumb_seq` int DEFAULT NULL COMMENT '썸네일순서',
  `type` enum('이미지','동영상','임베드') COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '타입 (''이미지'',''동영상'',''임베드'')',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '컨텐츠( URL, img, video, iframe )',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `product_no` (`product_no`),
  CONSTRAINT `media_ibfk_1` FOREIGN KEY (`product_no`) REFERENCES `products` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `option_group`
--

DROP TABLE IF EXISTS `option_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `option_group` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '옵션그룹명',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `options`
--

DROP TABLE IF EXISTS `options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `options` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `group_no` bigint NOT NULL COMMENT 'FK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '옵션명',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '옵션가격',
  `stock` bigint NOT NULL DEFAULT '0' COMMENT '옵션재고',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `group_no` (`group_no`),
  CONSTRAINT `options_ibfk_1` FOREIGN KEY (`group_no`) REFERENCES `option_group` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `product_no` bigint NOT NULL COMMENT 'FK',
  `order_no` bigint NOT NULL COMMENT 'FK',
  `quantity` bigint NOT NULL DEFAULT '1' COMMENT '수량',
  `price` decimal(10,2) NOT NULL COMMENT '가격',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `product_no` (`product_no`),
  KEY `order_no` (`order_no`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`product_no`) REFERENCES `products` (`no`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`order_no`) REFERENCES `orders` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `order_item_option`
--

DROP TABLE IF EXISTS `order_item_option`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item_option` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `order_item_no` bigint NOT NULL COMMENT 'FK: 주문항목',
  `option_no` bigint NOT NULL COMMENT 'FK: 선택한 옵션',
  `quantity` bigint NOT NULL DEFAULT '1' COMMENT '해당 옵션 수량',
  `price` decimal(10,2) NOT NULL COMMENT '해당 옵션 가격 (snapshot)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `order_item_no` (`order_item_no`),
  KEY `option_no` (`option_no`),
  CONSTRAINT `order_item_option_ibfk_1` FOREIGN KEY (`order_item_no`) REFERENCES `order_item` (`no`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_item_option_ibfk_2` FOREIGN KEY (`option_no`) REFERENCES `options` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `user_no` bigint NOT NULL COMMENT 'FK',
  `address_no` bigint DEFAULT NULL COMMENT 'FK',
  `shipment_no` bigint DEFAULT NULL COMMENT 'FK',
  `code` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '주문코드 (20250101_상품번호_유저번호_당일시퀀스)',
  `title` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주문제목 (상품1 외 5건)',
  `guest_tel` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '비회원 전화번호',
  `guest_email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '비회원 이메일',
  `guest_first_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '비회원 성',
  `guest_last_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '비회원 이름',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '총 가격',
  `total_quantity` bigint DEFAULT NULL COMMENT '총 수량',
  `total_item_count` bigint DEFAULT NULL COMMENT '총 항목수',
  `ship_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '배송비',
  `payment_method` enum('CASH','COIN','CARD','TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT 'TRANSFER' COMMENT '결제방식 (현금, 코인, 카드, 계좌이체)',
  `status` enum('결제대기','결제완료','배송준비중','배송시작','배송중','배송완료','주문취소','환불완료') COLLATE utf8mb4_unicode_ci DEFAULT '결제대기',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `user_no` (`user_no`),
  KEY `address_no` (`address_no`),
  KEY `orders_ibfk_3` (`shipment_no`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_no`) REFERENCES `users` (`no`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`address_no`) REFERENCES `address` (`no`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `orders_ibfk_3` FOREIGN KEY (`shipment_no`) REFERENCES `shipments` (`no`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `order_no` bigint NOT NULL COMMENT 'FK',
  `method` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '결제방식',
  `status` enum('결제대기','결제완료','결제실패','환불완료') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '결제대기' COMMENT '상태 ( ''결제대기'',''결제완료'',''결제실패'',''환불완료'' )',
  `payment_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '결제식별키',
  `amount` bigint NOT NULL COMMENT '결제금액',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `order_no` (`order_no`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_no`) REFERENCES `orders` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `persistent_logins`
--

DROP TABLE IF EXISTS `persistent_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persistent_logins` (
  `username` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `series` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `token` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_used` timestamp NOT NULL,
  PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `popups`
--

DROP TABLE IF EXISTS `popups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `popups` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '메인' COMMENT '타입',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이름',
  `url` text COLLATE utf8mb4_unicode_ci COMMENT '이미지경로',
  `link` text COLLATE utf8mb4_unicode_ci COMMENT '링크',
  `seq` int DEFAULT '0' COMMENT '순서',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '내용',
  `started_at` timestamp NULL DEFAULT NULL COMMENT '시작일',
  `ended_at` timestamp NULL DEFAULT NULL COMMENT '종료일',
  `is_show` tinyint(1) DEFAULT '1' COMMENT '공개여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '상품명',
  `stock` bigint DEFAULT '0' COMMENT '상품재고',
  `category_no` bigint NOT NULL COMMENT 'FK (카테고리)',
  `category_large_no` bigint NOT NULL COMMENT 'FK (대분류)',
  `option_group_no` bigint DEFAULT NULL COMMENT 'FK (옵션그룹)',
  `price` decimal(10,2) NOT NULL COMMENT '가격(기본가)',
  `ship_price` decimal(10,2) NOT NULL COMMENT '배송비',
  `ship_msg` text COLLATE utf8mb4_unicode_ci COMMENT '배송안내',
  `summary` text COLLATE utf8mb4_unicode_ci COMMENT '상품요약정보',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '상품상세(HTML)',
  `is_new` tinyint(1) DEFAULT NULL COMMENT '신상',
  `is_best` tinyint(1) DEFAULT NULL COMMENT '베스트',
  `is_sold_out` tinyint(1) DEFAULT NULL COMMENT '품절',
  `price_info` text COLLATE utf8mb4_unicode_ci COMMENT '가격설명',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sample`
--

DROP TABLE IF EXISTS `sample`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sample` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '이름',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seq`
--

DROP TABLE IF EXISTS `seq`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seq` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `seq_group_no` bigint NOT NULL COMMENT 'FK',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '시퀀스를 식별하는 코드 (seq_group_code)',
  `value` bigint NOT NULL DEFAULT '0' COMMENT '현재 시퀀스 번호',
  `date` date NOT NULL COMMENT '적용일자 (YYYY-MM-DD)',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `seq_group_no` (`seq_group_no`),
  CONSTRAINT `seq_ibfk_1` FOREIGN KEY (`seq_group_no`) REFERENCES `seq_groups` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seq_groups`
--

DROP TABLE IF EXISTS `seq_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seq_groups` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '시퀀스를 식별하는 코드',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '시퀀스 이름',
  `value` bigint NOT NULL DEFAULT '0' COMMENT '그룹 누적 시퀀스 번호',
  `step` bigint NOT NULL DEFAULT '1' COMMENT '증감치 (기본적으로 +1)',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '설명',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `user_no` bigint NOT NULL COMMENT 'FK',
  `tel` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '전화번호',
  `recipient` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '받는사람',
  `address` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '주소',
  `city` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '도시',
  `postcode` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '우편번호',
  `country` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '국가/지역',
  `is_main` tinyint(1) DEFAULT '0' COMMENT '기본배송지여부',
  `delivery_request` text COLLATE utf8mb4_unicode_ci COMMENT '배송요청사항',
  `delivery_method` text COLLATE utf8mb4_unicode_ci COMMENT '수령방법',
  `tracking_no` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ship_company` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('배송준비중','배송시작','배송중','배송완료','주문취소') COLLATE utf8mb4_unicode_ci DEFAULT '배송준비중' COMMENT '배송준비중, 배송시작, 배송중, 배송완료, 주문취소',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  KEY `user_no` (`user_no`),
  CONSTRAINT `shipments_ibfk_1` FOREIGN KEY (`user_no`) REFERENCES `users` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_auth`
--

DROP TABLE IF EXISTS `user_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_auth` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `user_no` bigint NOT NULL COMMENT 'FK',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '아이디',
  `auth` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '권한',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '이름',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  KEY `user_no` (`user_no`),
  CONSTRAINT `user_auth_ibfk_1` FOREIGN KEY (`user_no`) REFERENCES `users` (`no`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `no` bigint NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UK',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '아이디',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '비밀번호',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '이름',
  `first_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '성',
  `last_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '이름',
  `tel` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '전화번호',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '이메일',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '활성화여부',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일자',
  PRIMARY KEY (`no`),
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'falcon'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-17 10:22:42
