# Final Study Backend

## 프로젝트 소개
Spring Boot 4.0.6 + Java 25 기반 인증/인가 백엔드 프로젝트입니다.  
회원가입, 로그인, JWT 인증, Refresh Token 재발급, 로그아웃, 내 정보 조회 기능을 제공합니다.

## 기술 스택
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

## 주요 기능
- 회원가입
- 로그인
- JWT Access Token 인증
- Refresh Token 재발급
- 로그아웃
- 내 정보 조회
- 공통 API 응답 포맷
- 전역 예외 처리
- 요청 값 검증(Validation)
- Logging
- Swagger UI 문서
- 테스트(H2 기반 test profile)
- Docker 배포
- GitHub Actions CI/CD

## 로컬 실행 방법
1. Java 25, MySQL 8.x 설치
2. MySQL DB 생성 및 `application.yml` 수정 (자세한 내용: `docs/setup.md`)
3. 프로젝트 루트에서 실행

```bash
./gradlew bootRun
```

Windows:
```powershell
.\gradlew.bat bootRun
```

## 테스트 방법
```bash
./gradlew clean test
```

Windows:
```powershell
.\gradlew.bat clean test
```

테스트는 `test` 프로파일과 H2 메모리 DB를 사용하므로 외부 MySQL 의존성이 없습니다.

## Swagger 경로
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Docker 실행 방법
Docker Compose 실행 시 `SPRING_PROFILES_ACTIVE=docker`를 통해 `application-docker.yml`이 적용됩니다.

```bash
docker compose up -d --build
```

```bash
docker compose ps
docker compose logs -f app
```

## 문서
- API 명세: `docs/api-spec.md`
- 로컬 셋업: `docs/setup.md`
- 배포 가이드: `docs/deployment.md`
