# MSA Lecture

> 이 실습 코드는 교육 목적으로만 제공됩니다. 상용 서비스 수준의 완성본이 아니므로, 실제 배포 목적에 맞게 보완이 필요합니다.
> 문의: audit@korea.ac.kr (Sungryel Lim Ph.D)

## 아키텍처 / 기동 순서

```
MariaDB / Kafka (인프라)
  → Eureka (서비스 등록)
    → Auth Server (인증)
      → API Gateway + 4개 서비스
        → Recommend Service
```

`docker-compose.yml`의 `depends_on`을 따라 위 순서대로 컨테이너가 기동됩니다.

## 백엔드 실행

### 1. 공통 이미지 파일 로드 (API Gateway, Auth Server)

```bash
docker load -i infra-images.tar
docker images   # msa-lecture/auth-server:1.0 등 태그 확인
```

### 2. 빌드 & 기동

프로젝트 루트에서 실행합니다. (초기 트러블슈팅/리빌드 시 캐시 없이 빌드, 컨테이너는 묶어서 백그라운드로 실행)

```bash
docker compose build --no-cache
docker compose up -d

# 또는 한 줄로
docker compose build --no-cache && docker compose up -d
```

### 3. 로그 확인

```bash
# 전체 로그 한번에 보기
docker compose logs -f

# 개별 컨테이너 로그 보기
docker compose logs -f [서비스명]
```

| 서비스 | 로그 명령어 |
| --- | --- |
| MariaDB | `docker compose logs -f mariadb` |
| Kafka | `docker compose logs -f kafka` |
| Eureka Server | `docker compose logs -f eureka-server` |
| Auth Server | `docker compose logs -f auth-server` |
| API Gateway | `docker compose logs -f api-gateway` |
| User Service | `docker compose logs -f user-service` |
| Course Service | `docker compose logs -f course-service` |
| Enrollment Service | `docker compose logs -f enrollment-service` |
| Payment Service | `docker compose logs -f payment-service` |
| Recommend Service | `docker compose logs -f recommend-service` |

### 4. 전체 종료

```bash
docker compose down
```

컨테이너 빌드 중 실패했을 때 기존 컨테이너를 정리하는 용도로도 사용합니다.

### 5. 서버 기동 상태 확인

Eureka 대시보드: http://localhost:8761/

## 프론트엔드 실행

```bash
cd vue-frontend
pnpm install
pnpm dev
```

브라우저에서 접속: http://localhost:3000
