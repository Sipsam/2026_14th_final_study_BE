# API Specification

## Base URL
- `http://localhost:8080`

## 공통 응답 포맷
성공:
```json
{
  "success": true,
  "code": 200,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {}
}
```

실패:
```json
{
  "success": false,
  "code": 400,
  "message": "에러 메시지",
  "data": null
}
```

## 인증 헤더
```http
Authorization: Bearer {accessToken}
```

## Endpoints

### 1) 회원가입
- `POST /api/auth/signup`

Request:
```json
{
  "email": "test@example.com",
  "password": "password1234",
  "name": "홍길동"
}
```

### 2) 로그인
- `POST /api/auth/login`

Request:
```json
{
  "email": "test@example.com",
  "password": "password1234"
}
```

Response `data` 예시:
```json
{
  "userId": 1,
  "email": "test@example.com",
  "name": "홍길동",
  "accessToken": "....",
  "refreshToken": "....",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "refreshExpiresIn": 1209600000
}
```

### 3) 토큰 재발급
- `POST /api/auth/reissue`

Request:
```json
{
  "refreshToken": "...."
}
```

### 4) 로그아웃
- `POST /api/auth/logout`
- 인증 필요

### 5) 내 정보 조회
- `GET /api/users/me`
- 인증 필요

Response `data` 예시:
```json
{
  "id": 1,
  "email": "test@example.com",
  "name": "홍길동",
  "role": "USER",
  "createdAt": "2026-05-27T15:00:00"
}
```

## Swagger/OpenAPI
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
