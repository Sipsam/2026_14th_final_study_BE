# 2026 14th Final Study BE

## 1. 프로젝트 소개
Spring Boot 기반의 인증/인가 백엔드 프로젝트입니다.  
회원가입, 로그인, JWT 인증, Refresh Token 재발급, 로그아웃, 내 정보 조회 기능을 제공합니다.

## 2. 기술 스택
- Java 25
- Spring Boot 4.0.6
- Gradle
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT (jjwt)
- MySQL
- Swagger UI (springdoc-openapi)
- Docker / Docker Compose
- GitHub Actions (CI/CD)

## 3. 주요 기능
- 회원가입
- 로그인
- JWT Access Token 인증
- Refresh Token 재발급
- 로그아웃
- 내 정보 조회
- 공통 API 응답 포맷
- 전역 예외 처리
- 요청 값 검증(Validation)
- Logging 설정
- Swagger UI 문서화
- 테스트(H2 기반 test profile)
- Docker 배포
- GitHub Actions CI/CD

## 4. 로컬 실행 방법
1. Java 25, MySQL 8.x를 설치합니다.
2. MySQL에 `final_study_db`를 생성합니다. (아래 5번 참고)
3. `src/main/resources/application.yml` 값을 로컬 환경에 맞게 설정합니다. (아래 6번 참고)
4. 프로젝트 루트에서 실행합니다.

```bash
./gradlew bootRun
```

Windows:
```powershell
.\gradlew.bat bootRun
```

## 5. MySQL DB 생성 방법
```sql
CREATE DATABASE final_study_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

## 6. application.yml 설정 방법
로컬 개발용 `application.yml`은 **환경변수를 사용하지 않고 값을 직접 작성**하는 방식입니다.

주요 확인 항목:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret-key`
- `jwt.access-token.expiration`
- `jwt.refresh-token.expiration`

예시(`src/main/resources/application.yml`):
- `spring.datasource.url=jdbc:mysql://localhost:3306/final_study_db?...`
- `spring.datasource.username=root`
- `spring.datasource.password=change-me`

보안 안내:
- 현재 방식은 로컬/학습 목적에 맞춘 구성입니다.
- 실제 운영 환경에서는 DB 비밀번호, JWT 시크릿 같은 민감정보를 **환경변수** 또는 **서버 전용 설정 파일**로 분리하는 것을 권장합니다.

## 7. 테스트 실행 방법
```bash
./gradlew clean test
```

Windows:
```powershell
.\gradlew.bat clean test
```

테스트는 `test` 프로파일(`src/test/resources/application-test.yml`)의 H2 메모리 DB를 사용하므로 외부 MySQL에 의존하지 않습니다.

## 8. Swagger UI 접속 경로
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 9. API 명세서 위치
- [docs/api-spec.md](D:/IdeaProjects/2026_14th_final_study_BE/docs/api-spec.md)

## 10. Docker 실행 방법
Docker Compose 실행 시 `SPRING_PROFILES_ACTIVE=docker`가 적용되어 `application-docker.yml`을 사용합니다.

```bash
docker compose up -d --build
```

```bash
docker compose ps
docker compose logs -f app
```

Docker DB URL은 다음 형식을 사용합니다.
- `jdbc:mysql://mysql:3306/final_study_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true`

## 11. Vultr 배포 구조
- 배포 방식: GitHub Actions에서 Vultr Ubuntu 서버로 SSH 접속 후 Docker Compose 재배포
- 워크플로우: `.github/workflows/deploy.yml`
- 트리거: `week8/#6-jangsungwon` 브랜치 push
- 서버 실행 순서:
  1. 프로젝트 디렉터리 이동
  2. `git pull origin week8/#6-jangsungwon`
  3. `docker compose down`
  4. `docker compose up -d --build`
  5. `docker ps`
  6. `/actuator/health` (실패 시 `/v3/api-docs`) 확인

필수 GitHub Secrets:
- `SSH_HOST`
- `SSH_USERNAME`
- `SSH_PRIVATE_KEY`

참고 문서:
- [docs/deployment.md](D:/IdeaProjects/2026_14th_final_study_BE/docs/deployment.md)
