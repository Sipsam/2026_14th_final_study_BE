# API Spec (Frontend)

## 1) 프로젝트 API 기본 정보

- 프로젝트명: Final Study Backend API
- 목적: 회원 인증/인가(JWT + Refresh Token) 및 사용자 정보 조회
- 환경: 로컬 개발 기준
- **중요**: 현재 프로젝트는 `application.yml`에 DB/JWT 값을 직접 작성하는 로컬 개발 구조입니다.

## 2) Base URL

```json
{
  "baseUrl": "http://localhost:8080"
}
```

## 3) 공통 응답 형식

```json
{
  "success": true,
  "code": 200,
  "message": "요청이 성공적으로 처리되었습니다.",
  "data": {}
}
```

## 4) 공통 에러 응답 형식

```json
{
  "success": false,
  "code": 400,
  "message": "에러 메시지",
  "data": null
}
```

## 5) 인증 방식

```json
{
  "type": "Bearer Token",
  "tokenFlow": {
    "login": "accessToken + refreshToken 발급",
    "reissue": "refreshToken으로 accessToken + refreshToken 재발급",
    "logout": "서버 저장 refreshToken 삭제"
  }
}
```

## 6) Authorization Header 형식

```json
{
  "Authorization": "Bearer {accessToken}"
}
```

---

## 7) 회원가입 API

### Method

```json
{
  "method": "POST"
}
```

### URL

```json
{
  "url": "/api/auth/signup"
}
```

### Description

```json
{
  "description": "이메일, 비밀번호, 이름으로 회원가입합니다."
}
```

### Request Headers

```json
{
  "Content-Type": "application/json"
}
```

### Request Body

```json
{
  "email": "test@example.com",
  "password": "password1234",
  "name": "홍길동"
}
```

### Success Response

```json
{
  "success": true,
  "code": 200,
  "message": "회원가입에 성공했습니다.",
  "data": {
    "id": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "role": "USER",
    "createdAt": "2026-05-27T15:00:00"
  }
}
```

### Error Response

```json
{
  "success": false,
  "code": 409,
  "message": "이미 사용 중인 이메일입니다.",
  "data": null
}
```

---

## 8) 로그인 API

### Method

```json
{
  "method": "POST"
}
```

### URL

```json
{
  "url": "/api/auth/login"
}
```

### Description

```json
{
  "description": "이메일/비밀번호 검증 후 Access/Refresh Token을 발급합니다."
}
```

### Request Headers

```json
{
  "Content-Type": "application/json"
}
```

### Request Body

```json
{
  "email": "test@example.com",
  "password": "password1234"
}
```

### Success Response

```json
{
  "success": true,
  "code": 200,
  "message": "로그인에 성공했습니다.",
  "data": {
    "userId": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "accessToken": "<ACCESS_TOKEN>",
    "refreshToken": "<REFRESH_TOKEN>",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "refreshExpiresIn": 1209600000
  }
}
```

### Error Response

```json
{
  "success": false,
  "code": 404,
  "message": "사용자를 찾을 수 없습니다.",
  "data": null
}
```
```
{
  "success": false,
  "code": 400,
  "message": "비밀번호가 올바르지 않습니다.",
  "data": null
}
```

---

## 9) 토큰 재발급 API

### Method

```json
{
  "method": "POST"
}
```

### URL

```json
{
  "url": "/api/auth/reissue"
}
```

### Description

```json
{
  "description": "Refresh Token 검증 후 Access/Refresh Token을 재발급합니다."
}
```

### Request Headers

```json
{
  "Content-Type": "application/json"
}
```

### Request Body

```json
{
  "refreshToken": "<REFRESH_TOKEN>"
}
```

### Success Response

```json
{
  "success": true,
  "code": 200,
  "message": "토큰 재발급에 성공했습니다.",
  "data": {
    "userId": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "accessToken": "<NEW_ACCESS_TOKEN>",
    "refreshToken": "<NEW_REFRESH_TOKEN>",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "refreshExpiresIn": 1209600000
  }
}
```

### Error Response

```json
{
  "success": false,
  "code": 401,
  "message": "만료된 리프레시 토큰입니다.",
  "data": null
}
```
```
{
  "success": false,
  "code": 404,
  "message": "리프레시 토큰을 찾을 수 없습니다.",
  "data": null
}
```

---

## 10) 로그아웃 API

### Method

```json
{
  "method": "POST"
}
```

### URL

```json
{
  "url": "/api/auth/logout"
}
```

### Description

```json
{
  "description": "현재 사용자 refreshToken을 서버 DB에서 삭제합니다."
}
```

### Request Headers

```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer {accessToken}"
}
```

### Request Body

```json
{}
```

### Success Response

```json
{
  "success": true,
  "code": 200,
  "message": "로그아웃에 성공했습니다.",
  "data": null
}
```

### Error Response

```json
{
  "success": false,
  "code": 401,
  "message": "인증이 필요합니다.",
  "data": null
}
```

---

## 11) 내 정보 조회 API

### Method

```json
{
  "method": "GET"
}
```

### URL

```json
{
  "url": "/api/users/me"
}
```

### Description

```json
{
  "description": "JWT 인증 정보를 기반으로 현재 로그인한 사용자 정보를 조회합니다."
}
```

### Request Headers

```json
{
  "Authorization": "Bearer {accessToken}"
}
```

### Request Body

```json
{}
```

### Success Response

```json
{
  "success": true,
  "code": 200,
  "message": "내 정보 조회에 성공했습니다.",
  "data": {
    "id": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "role": "USER",
    "createdAt": "2026-05-27T15:00:00"
  }
}
```

### Error Response

```json
{
  "success": false,
  "code": 401,
  "message": "인증이 필요합니다.",
  "data": null
}
```
```
{
  "success": false,
  "code": 404,
  "message": "사용자를 찾을 수 없습니다.",
  "data": null
}
```

---

## 12) Validation 실패 예시

```json
{
  "success": false,
  "code": 400,
  "message": "email: 이메일 형식이 올바르지 않습니다.",
  "data": null
}
```

```json
{
  "success": false,
  "code": 400,
  "message": "password: 비밀번호는 8자 이상이어야 합니다.",
  "data": null
}
```

---

## 13) 인증 실패 예시

```json
{
  "success": false,
  "code": 401,
  "message": "유효하지 않은 토큰입니다.",
  "data": null
}
```

```json
{
  "success": false,
  "code": 401,
  "message": "만료된 토큰입니다.",
  "data": null
}
```

---

## 14) Swagger UI 접속 경로

```json
{
  "swaggerUi": "http://localhost:8080/swagger-ui.html",
  "openApiDocs": "http://localhost:8080/v3/api-docs"
}
```

