DELETE FROM User;
ALTER TABLE User AUTO_INCREMENT = 1;
DELETE FROM Authority;
ALTER TABLE Authority AUTO_INCREMENT = 1;
DELETE FROM Payment;
ALTER TABLE Payment AUTO_INCREMENT = 1;
DELETE FROM Plan;
ALTER TABLE Plan AUTO_INCREMENT = 1;
DELETE FROM Post;
ALTER TABLE Post AUTO_INCREMENT = 1;
DELETE FROM Attachment;
ALTER TABLE Attachment AUTO_INCREMENT = 1;
DELETE FROM BrandAttachment;
ALTER TABLE BrandAttachment AUTO_INCREMENT = 1;
DELETE FROM ItemAttachment;
ALTER TABLE ItemAttachment AUTO_INCREMENT = 1;
DELETE FROM Comment;
ALTER TABLE Comment AUTO_INCREMENT = 1;
DELETE FROM Item;
ALTER TABLE Item AUTO_INCREMENT = 1;
DELETE FROM Rental;
ALTER TABLE Rental AUTO_INCREMENT = 1;
DELETE FROM Brand;
ALTER TABLE Brand AUTO_INCREMENT = 1;


INSERT INTO Authority(grade)
VALUES ('USER'),
       ('BRAND'),
       ('ADMIN')
;

INSERT INTO Plan(type, price, count)
VALUES ('SILVER', 50000, 3),
       ('GOLD', 70000, 5),
       ('VIP', 100000, 10)
;

INSERT INTO User (User.auth_id, User.plan_id, username, password, name, zipcode, address, address_detail, phone_num, point, status_plan, status_account, paid_at, rental_cnt)
VALUES (3, NULL, 'admin', '$2a$10$.JN4oKC7Nr6oR8NgYxX3fOvtAn3OOURyYPNDf4Y/E5hfWKhblkKfe', '관리자', NULL, NULL, NULL, NULL, 0, 'INACTIVE', 'ACTIVE', NULL, 0),
       (1, 1, 'user1', '$2a$10$AsdcGiiMWwG6sCu9IiNqvu5Z1G7krhWLhehijgfiqjRhHCODctw8a', '회원1', '63534', '제주특별자치도 서귀포시 가가로 14', '101호', '010-1111-1111', 30000, 'ACTIVE', 'INACTIVE', '2025-4-24', 2),
       (1, 2, 'user2', '$2a$10$5e2fLl7OQKtTpGQyIlvbMuI8.eyKlVu1qfRuHlC/QyIcLvdPgh48O', '회원2', '13480', '경기 성남시 분당구 대왕판교로 477', '1층', '010-2222-2222', 98700, 'ACTIVE', 'ACTIVE', '2025-5-1', 3),
       (1, NULL, 'user3', '$2a$10$zT51nN0ycpAvSg5aimPoUuOIyg94ktXJzLhWHGxeJQ8iDqXB4vqRm', '회원3', '07761', '서울 강서구 가로공원로 172', '103동 1101호', '010-3333-3333', 0, 'INACTIVE', 'ACTIVE', NULL, 0),
       (1, NULL, 'user4', '$2a$10$3E9ibPMOysRko4WHfCxTw.Ls4PjNnjSHL.LygIf4dotmxDWDABsmW', '회원4', '08200', '서울 구로구 안양천동자전거길 525', '503호', '010-4444-4444', 0, 'INACTIVE', 'ACTIVE', NULL, 0)
;

INSERT INTO Brand (Brand.auth_id, name, username, password, phone_num, is_actived, description)
VALUES (2, '브랜드1', 'brand1', '$2a$10$LJQ8kSeSfv2anNw8yFYOkejXXY78jd8KT9eQyP2rfI5fo9.FrHe0e', '031-1234-5678', true, 'brand1 입니다.'),
       (2, '브랜드2', 'brand2', '$2a$10$6H6Ff0AGq8PN6lI.1PFa5O.m6DMLo6nd.ZNyc9zzedruNF8SjKfd2', '02-1111-2222', true, 'brand2 입니다.'),
       (2, '브랜드3', 'brand3', '$2a$10$w8sCZyjvga4hxJYLlq3aSOzHGkwQmwEpDu87bUDdgD6CJv5kfhXpC', '02-1111-2222', true, 'brand3 입니다.')
;

INSERT INTO BrandAttachment(brand_id, sourcename, filename)
VALUES (1, 'face01.png', '6737b830-9b99-468f-8541-db39519b6ddf_face01.png'),
       (2, 'face02.png', '462a2e1d-1c11-4608-bd64-a71bedb0ba17_face02.png')
;

-- 포인트 조회 기능 확인용 샘플 데이터
UPDATE User
SET point = 30000
WHERE username = 'user5';

UPDATE User SET point = 30000 WHERE id = 7;
