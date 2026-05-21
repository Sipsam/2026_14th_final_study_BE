# Final Study Backend - Spring Boot 프로젝트 구조

## 📋 프로젝트 개요

- **Framework**: Spring Boot 4.0.6
- **Java Version**: Java 25
- **Build Tool**: Gradle
- **Database**: MySQL
- **Authentication**: Spring Security + JWT + Refresh Token
- **API Documentation**: Swagger/OpenAPI 3.0

## 📁 패키지 구조

```
com.likelion.finalstudy/
├── FinalStudyApplication.java          # Main 애플리케이션 클래스
├── domain/                              # 도메인 엔티티
│   └── user/
│       └── User.java
├── repository/                          # Spring Data JPA Repository
│   └── UserRepository.java
├── service/                             # 비즈니스 로직 (미작성)
├── controller/                          # REST API Controller (미작성)
├── dto/                                 # Data Transfer Object (미작성)
└── global/                              # 공통 설정
    ├── config/                          # Spring 설정
    │   ├── SecurityConfig.java          # Spring Security 설정
    │   ├── WebConfig.java               # Web MVC 설정
    │   └── SwaggerConfig.java           # Swagger/OpenAPI 설정
    ├── exception/                       # 예외 처리
    │   ├── BusinessException.java       # 커스텀 예외
    │   └── GlobalExceptionHandler.java  # 전역 예외 핸들러
    ├── jwt/                             # JWT 관련 설정
    │   ├── JwtProvider.java             # JWT 토큰 생성/검증
    │   └── JwtFilter.java               # JWT 필터
    ├── logging/                         # 로깅 설정
    │   └── HttpLoggingInterceptor.java  # HTTP 요청/응답 로깅
    └── response/                        # API 응답
        └── ApiResponse.java             # 공통 API 응답 형식
```

## 🔧 주요 설정 파일

### application.yml

- MySQL 데이터베이스 연결 설정
- JPA Hibernate 설정 (DDL: create)
- JWT 설정
    - Secret Key: `your-secret-key-your-secret-key-your-secret-key`
    - Access Token 만료시간: 3600000ms (1시간)
- Swagger/OpenAPI 설정
- 로깅 레벨 설정

### build.gradle

의존성:

- **Spring Web MVC**: REST API 개발
- **Spring Data JPA**: 데이터베이스 접근
- **Spring Security**: 인증/인가
- **MySQL Driver**: MySQL 연결
- **Lombok**: 보일러플레이트 코드 감소
- **Validation**: Bean Validation
- **JWT**: 토큰 기반 인증
- **Swagger/OpenAPI**: API 문서화
- **Test**: JUnit 5, Spring Boot Test

## 🚀 주요 기능

### 1. 공통 응답 형식 (ApiResponse)

```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

### 2. JWT 인증

- Token 생성 및 검증
- Bearer token 형식 사용
- 자동 로그인 상태 유지

### 3. 예외 처리

- 비즈니스 예외 (BusinessException)
- 전역 예외 핸들러 (GlobalExceptionHandler)

### 4. HTTP 로깅

- 요청/응답 자동 로깅
- HTTP Interceptor 사용

### 5. Swagger UI

- 경로: `/swagger-ui.html`
- API 문서: `/v3/api-docs`
- JWT Bearer 토큰 지원

## 📝 필요한 추가 구현 (TODO)

### 1. Service Layer

- UserService
- AuthenticationService
- 비즈니스 로직 구현

### 2. Controller Layer

- AuthController (로그인, 회원가입, 토큰 갱신)
- UserController (사용자 정보 조회)
- 다른 도메인 컨트롤러

### 3. DTO

- LoginRequest, LoginResponse
- UserRegisterRequest
- UserResponse
- 기타 도메인 DTO

### 4. 추가 도메인 엔티티

- 프로젝트 요구사항에 따라 추가

### 5. JPA Auditing

- @CreationTimestamp, @UpdateTimestamp 자동 설정
- 작성자/수정자 정보 추적

### 6. Refresh Token 구현

- 별도의 Refresh Token 엔티티
- Token Rotation 전략

## 🔐 Security 설정

### 인증 필요 없는 경로

- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/swagger-resources/**`
- `/actuator/**`

### 인증 필요한 경로

- 그 외 모든 API 엔드포인트

## 🗄️ 데이터베이스 설정

### MySQL 연결 정보

- **Host**: localhost
- **Port**: 3306
- **Database**: final_study_db
- **Username**: root
- **Password**: 1234
- **Charset**: UTF-8
- **Timezone**: Asia/Seoul

### 테이블 생성

- JPA Hibernate `ddl-auto: create` 설정으로 자동 생성
- 개발 환경에서만 사용 권장
- 운영 환경에서는 `validate` 또는 `update` 사용

## 🛠️ 개발 환경 구성

### 필요한 도구

1. JDK 25 이상
2. Gradle 8.x
3. MySQL 8.0 이상
4. IDE (IntelliJ IDEA, VS Code 등)

### 프로젝트 빌드

```bash
./gradlew clean build -x test
```

### 프로젝트 실행

```bash
./gradlew bootRun
```

### 테스트 실행

```bash
./gradlew test
```

## 📚 API 문서

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🔍 기본 엔티티

### User Entity

- id: Long (PK)
- username: String (Unique)
- password: String
- email: String (Unique)
- isActive: Boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime

## 📝 변경 사항 요약

### 변경된 파일

1. ✅ `build.gradle` - 의존성 정리 및 추가
2. ✅ `application.yml` - MySQL 설정 추가, 로깅 설정 추가
3. ✅ `FinalApplication.java` → `FinalStudyApplication.java` (패키지 변경)

### 생성된 파일

- ✅ Global Configuration (Security, Web, Swagger)
- ✅ JWT 관련 클래스 (JwtProvider, JwtFilter)
- ✅ Exception Handling (BusinessException, GlobalExceptionHandler)
- ✅ API Response (ApiResponse)
- ✅ Logging (HttpLoggingInterceptor)
- ✅ Domain & Repository (User, UserRepository)
- ✅ Test (FinalStudyApplicationTests)

### 삭제된 파일

- ❌ `com.example.demo` 패키지 및 샘플 코드 (호환성 유지 중)

## 🚨 주의사항

1. **JWT Secret Key**: 프로덕션 환경에서는 환경변수로 관리하세요
2. **Database 설정**: 로컬 MySQL이 실행 중이어야 합니다
3. **CORS 설정**: 필요시 `WebConfig`에 CORS 설정을 추가하세요
4. **DB 초기화**: `ddl-auto: create` 사용 시 매 시작마다 테이블이 재생성됩니다

## 📖 참고 문서

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Spring Security 문서](https://spring.io/projects/spring-security)
- [JWT 문서](https://tools.ietf.org/html/rfc7519)
- [Swagger 공식 문서](https://swagger.io/)

