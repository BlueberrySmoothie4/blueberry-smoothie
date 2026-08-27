# api-gateway-config

api-gateway는 소스가 없는 prebuilt 이미지(`msa-lecture/api-gateway:1.0`)라서
라우팅 규칙(`/api/courses/**`, `/api/enrollments/**`)을 코드로 고칠 수 없다.

대신 컨테이너 WorkingDir(`/app`) 기준의 `./config/application.yml` 을 볼륨으로 마운트한다.
Spring Boot 기본 설정 탐색 경로(`optional:file:./config/`)가 jar 내부 classpath보다
우선순위가 높으므로, 여기 정의한 `spring.cloud.gateway.routes` 가 이미지 안의 것을 대체한다.

주의: `routes` 는 리스트라서 **부분 덮어쓰기가 안 된다.** 항상 6개 라우트를 전부 적어야 한다.
