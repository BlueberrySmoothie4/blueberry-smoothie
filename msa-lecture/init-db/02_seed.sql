-- AI API 마켓플레이스 시드 데이터
-- 그룹 계열사 간 사내 AI API 마켓플레이스 시나리오
--
-- 실행 시점: MariaDB 최초 기동 시 01_init.sql 다음 (파일명 알파벳 순).
--            볼륨이 비어 있을 때만 실행되므로 재적용하려면 docker compose down -v 필요.
--
-- id 는 전부 AUTO_INCREMENT 에 맡기고, 상품은 제공사를 이메일로 찾아 참조한다.
-- (api_products.provider_id 는 users.id 를 FK 로 참조)
--
-- ⚠️ users 를 여기서 넣는 이유
--    이 스크립트가 도는 시점엔 아직 어떤 서비스도 뜨지 않아 users 가 비어 있다.
--    상품만 넣으면 FK 위반(ERROR 1452)으로 실패한다.
--
-- ⚠️ 부작용
--    auth-server 의 DataInitializer 는 users.count() == 0 일 때만 기본 계정을 만든다.
--    여기서 미리 넣으면 auth-server 는 시딩을 건너뛴다. 그래서 실습 원본 기본 계정 2개를
--    동일한 이메일·해시로 포함시켰다 — 빼면 student@/instructor@lecture.com 로그인이 안 된다.
--
-- ⚠️ role 컬럼은 반드시 레거시 값(STUDENT/INSTRUCTOR)으로 넣을 것.
--    도메인상 BUYER/PROVIDER 지만 물리 저장값은 user-service 의 RoleConverter 가 변환한다.
--    여기에 'PROVIDER' 를 쓰면 auth-server 가 enum 파싱에 실패해 로그인이 깨진다.
--
-- 모든 계정 비밀번호: password1234
-- 이메일 도메인은 문서용 예약 도메인(example.com)을 사용한다.

-- INSERT IGNORE: email 이 UNIQUE 이므로, 이미 뜬 DB에 수동 적용할 때 중복 계정은 건너뛴다.
INSERT IGNORE INTO users (email, password, name, role, created_at, updated_at) VALUES
  -- 실습 원본 기본 계정 (auth-server 가 만들던 것과 동일 — 호환용)
  ('student@lecture.com',    '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '홍길동',        'STUDENT',    NOW(), NOW()),
  ('instructor@lecture.com', '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '김강사',        'INSTRUCTOR', NOW(), NOW()),
  -- API 제공 계열사 (PROVIDER)
  ('ax@example.com',         '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', 'SK AX',        'INSTRUCTOR', NOW(), NOW()),
  ('skt@example.com',        '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', 'SK텔레콤',      'INSTRUCTOR', NOW(), NOW()),
  ('hynix@example.com',      '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', 'SK하이닉스',    'INSTRUCTOR', NOW(), NOW()),
  ('shieldus@example.com',   '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', 'SK쉴더스',      'INSTRUCTOR', NOW(), NOW()),
  ('skinno@example.com',     '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', 'SK이노베이션',  'INSTRUCTOR', NOW(), NOW()),
  ('skbb@example.com',       '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', 'SK브로드밴드',  'INSTRUCTOR', NOW(), NOW()),
       -- 중소 기업
    ('techinno@example.com', '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '(주)테크이노베이션', 'STUDENT', NOW(), NOW()),
    ('miresoft@example.com', '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '(주)미래소프트', 'STUDENT', NOW(), NOW()),
    ('smartsol@example.com', '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '(주)스마트솔루션즈', 'STUDENT', NOW(), NOW()),
    ('ecobiz@example.com', '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '(주)에코비즈', 'STUDENT', NOW(), NOW()),
    ('globalnet@example.com', '$2a$10$3nkxWTZljNk6IP6NNyfWguKoov27uNEMUuopqXnxlDx64mtezkTcW', '(주)글로벌네트웍스', 'STUDENT', NOW(), NOW());



-- API 상품
-- subscriber_count 는 인기순 정렬 데모를 위해 서로 다르게 넣는다.
-- VISION 과 ANOMALY 는 제공 계열사가 둘씩이라 "멀티벤더 대체재 추천" 시연이 가능하다.
INSERT INTO api_products (title, description, category, price, provider_id, subscriber_count, status, created_at, updated_at) VALUES
  ('웨이퍼 표면 결함 검출 API', '반도체 웨이퍼 이미지에서 표면 결함을 검출하고 유형별로 분류합니다.',  'VISION',     89000, (SELECT id FROM users WHERE email='hynix@example.com'),    42, 'PUBLISHED',  NOW(6), NOW(6)),
  ('한국어 통화 STT API',       '고객센터 통화 음성을 한국어 텍스트로 변환합니다. 실시간 스트리밍 지원.', 'SPEECH',     69000, (SELECT id FROM users WHERE email='skt@example.com'),      31, 'PUBLISHED',  NOW(6), NOW(6)),
  ('공정 이미지 분류 API',      '생산 라인 카메라 이미지를 공정 단계별로 자동 분류합니다.',            'VISION',     59000, (SELECT id FROM users WHERE email='hynix@example.com'),    18, 'PUBLISHED',  NOW(6), NOW(6)),
  ('상품 설명 생성 API',        '상품 속성 몇 개만 입력하면 판매 채널별 홍보 문구를 생성합니다.',        'GENERATIVE', 75000, (SELECT id FROM users WHERE email='skt@example.com'),      21, 'PUBLISHED',  NOW(6), NOW(6)),
  ('계약서 조항 추출 API',      'PDF 계약서에서 조항을 단위별로 추출하고 검토가 필요한 항목을 표시합니다.','DOCUMENT',   75000, (SELECT id FROM users WHERE email='ax@example.com'),       27, 'PUBLISHED',  NOW(6), NOW(6)),
  ('영상 콘텐츠 객체 탐지 API',  '동영상 프레임에서 인물·사물·로고를 탐지해 타임코드와 함께 반환합니다.', 'VISION',     74000, (SELECT id FROM users WHERE email='skbb@example.com'),     25, 'PUBLISHED',  NOW(6), NOW(6)),
  ('설비 이상 탐지 API',        '설비 센서 시계열에서 고장 전조 패턴을 탐지합니다.',                   'ANOMALY',    82000, (SELECT id FROM users WHERE email='skinno@example.com'),    9, 'PUBLISHED',  NOW(6), NOW(6)),
  ('화자 분리 API',            '다중 화자 녹음에서 발화자를 구분해 세그먼트 단위로 나눕니다.',          'SPEECH',     45000, (SELECT id FROM users WHERE email='skt@example.com'),      12, 'PUBLISHED',  NOW(6), NOW(6)),
  ('다국어 번역·요약 API',      '14개 언어 번역과 문단 단위 요약을 하나의 엔드포인트로 제공합니다.',     'LANGUAGE',   99000, (SELECT id FROM users WHERE email='ax@example.com'),       55, 'PUBLISHED',  NOW(6), NOW(6)),
  ('전표·영수증 OCR API',       '전표와 영수증 이미지에서 품목·금액·일자를 구조화된 JSON으로 반환합니다.','DOCUMENT',   39000, (SELECT id FROM users WHERE email='ax@example.com'),       63, 'PUBLISHED',  NOW(6), NOW(6)),
  ('에너지 수요 예측 API',      '과거 사용량을 기반으로 향후 12주 에너지 수요를 예측합니다.',           'FORECAST',   59000, (SELECT id FROM users WHERE email='skinno@example.com'),   16, 'PUBLISHED',  NOW(6), NOW(6)),
  ('이상 거래 탐지 API',        '결제·인증 로그에서 비정상 패턴을 탐지해 위험 점수를 반환합니다.',        'ANOMALY',    67000, (SELECT id FROM users WHERE email='shieldus@example.com'), 34, 'PUBLISHED',  NOW(6), NOW(6)),
  -- 지원 종료 상품: 목록(PUBLISHED만 조회)에서 빠지는 것을 보여주는 용도
  ('레거시 문서 OCR API v1',    '2026-12 지원 종료 예정. 전표·영수증 OCR API 로 이전을 권장합니다.',    'DOCUMENT',   19000, (SELECT id FROM users WHERE email='ax@example.com'),        3, 'DEPRECATED', NOW(6), NOW(6));
