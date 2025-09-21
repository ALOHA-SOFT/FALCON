-- Active: 1754231727979@@falcon-db.cr8aiiek0cvi.eu-west-2.rds.amazonaws.com@3306@falcon
-- 상품 전체 조회해서 name 을 Product01, Product02, ... 로 업데이트
UPDATE products
SET name = CONCAT('Product', LPAD(no, 2, '0'));
