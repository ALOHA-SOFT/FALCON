-- Active: 1750388006843@@falcon-db.cr8aiiek0cvi.eu-west-2.rds.amazonaws.com@3306@falcon
USE `falcon`;


SET FOREIGN_KEY_CHECKS = 0;

-- 기본 데이터
TRUNCATE TABLE `users`;
TRUNCATE TABLE `user_auth`;



-- BCryptPasswordEncoder - 암호화 시
-- 사용자
INSERT INTO users ( id, username, password, name, first_name, last_name, tel, email )
VALUES ( UUID(), 'user', '$2a$12$TrN..KcVjciCiz.5Vj96YOBljeVTTGJ9AUKmtfbGpgc9hmC7BxQ92', '김사용', 'John', 'Kim', '010-1234-5678', 'user@mail.com' );

-- 관리자
INSERT INTO users ( id, username, password, name, first_name, last_name, tel, email )
VALUES ( UUID(), 'admin', '$2a$12$TrN..KcVjciCiz.5Vj96YOBljeVTTGJ9AUKmtfbGpgc9hmC7BxQ92', '박관리', 'David', 'Park', '010-9876-5432', 'admin@mail.com' );


-- 테스트
INSERT INTO users ( id, username, password, name, first_name, last_name, tel, email )
VALUES ( UUID(), 'test', '$2a$12$TrN..KcVjciCiz.5Vj96YOBljeVTTGJ9AUKmtfbGpgc9hmC7BxQ92', '이테스트', 'Michael', 'Lee', '010-1111-2222', 'test@mail.com' );



-- 권한
-- 사용자 
-- * 권한 : ROLE_USER
INSERT INTO user_auth ( id, user_no, username,  auth, name )
VALUES ( UUID(), (SELECT no FROM users WHERE username = 'user'), 'user', 'ROLE_USER', '사용자' );

-- 관리자
-- * 권한 : ROLE_USER, ROLE_ADMIN
INSERT INTO user_auth ( id, user_no, username,  auth, name )
VALUES ( UUID(), (SELECT no FROM users WHERE username = 'admin'), 'admin', 'ROLE_USER', '사용자' );

INSERT INTO user_auth ( id, user_no, username,  auth, name )
VALUES ( UUID(), (SELECT no FROM users WHERE username = 'admin'), 'admin', 'ROLE_ADMIN', '관리자' );

-- 사용자 
-- * 권한 : ROLE_USER
INSERT INTO user_auth ( id, user_no, username,  auth, name )
VALUES ( UUID(), (SELECT no FROM users WHERE username = 'test'), 'test', 'ROLE_USER', '사용자' );


SET FOREIGN_KEY_CHECKS = 1;