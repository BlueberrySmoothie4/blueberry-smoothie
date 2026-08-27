-- AI API 마켓플레이스 초기 DDL
-- Spring JPA ddl-auto: update 로도 생성되지만
-- 명시적 DDL로 테이블 선후 관계를 문서화

-- role 컬럼값은 레거시 문자열(STUDENT | INSTRUCTOR)을 그대로 유지한다.
-- 도메인상 역할은 BUYER | PROVIDER 이지만, prebuilt auth-server 이미지가
-- 같은 테이블을 STUDENT/INSTRUCTOR enum으로 읽기 때문에 물리 저장값만 고정하고
-- user-service의 RoleConverter가 BUYER/PROVIDER 로 변환한다.
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255)    NOT NULL UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    name        VARCHAR(100)    NOT NULL,
    role        VARCHAR(20)     NOT NULL COMMENT 'STUDENT(=BUYER) | INSTRUCTOR(=PROVIDER)',
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- API 제공사가 상품 등록 (provider_id → users.id)
CREATE TABLE IF NOT EXISTS api_products (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    title            VARCHAR(255)    NOT NULL,
    description      TEXT,
    category         VARCHAR(50)     NOT NULL COMMENT 'VISION|SPEECH|LANGUAGE|DOCUMENT|GENERATIVE|FORECAST|ANOMALY|OTHER',
    price            DECIMAL(10,2)   NOT NULL COMMENT '월 구독료 (정액제)',
    provider_id      BIGINT          NOT NULL,
    subscriber_count INT             NOT NULL DEFAULT 0,
    status           VARCHAR(20)     NOT NULL DEFAULT 'PUBLISHED' COMMENT 'PUBLISHED | DEPRECATED',
    created_at       DATETIME(6),
    updated_at       DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (provider_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 구매 기업이 구독 신청 (user_id → users.id, product_id → api_products.id)
CREATE TABLE IF NOT EXISTS subscriptions (
    id          BIGINT      NOT NULL AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    product_id  BIGINT      NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING | ACTIVE | CANCELLED',
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_product (user_id, product_id),
    FOREIGN KEY (user_id)    REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES api_products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 구독 확정 후 결제 (user_id → users.id, product_id → api_products.id)
CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT          NOT NULL,
    product_id      BIGINT          NOT NULL,
    amount          DECIMAL(10,2)   NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING | COMPLETED | FAILED | CANCELLED',
    transaction_id  VARCHAR(255)    UNIQUE,
    created_at      DATETIME(6),
    updated_at      DATETIME(6),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id)    REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES api_products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
