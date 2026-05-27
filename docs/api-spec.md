# Final Study Backend API Specification

## 1. 문서 개요

| 항목 | 내용 |
|---|---|
| 프로젝트명 | Final Study Backend |
| API 목적 | 회원가입/로그인/JWT 인증/토큰 재발급/로그아웃/내 정보 조회 기능 제공 |
| 인증 방식 | JWT Bearer Access Token + Refresh Token |
| 공통 응답 형식 | `ApiResponse<T>` |
| 문서 기준 버전 | OpenAPI Info `1.0.0` (코드 기준 작성일: 2026-05-27) |

본 문서는 실제 코드(`Controller`, `DTO`, `ApiResponse`, `Security`, `Exception Handler`) 기준으로 작성되었습니다.

---

## 2. 서버 주소

| 환경 | 주소 예시 |
|---|---|
| Local | `http://localhost:8080` |
| Docker(로컬 실행) | `http://localhost:8080` |
| Docker 내부 네트워크(MySQL host) | `mysql` (DB 접속용 service name) |
| Vultr 배포 | `http://<VULTR_PUBLIC_IP>:8080` 또는 `https://<DOMAIN>` |
| Swagger UI | `<BASE_URL>/swagger-ui.html` |
| API Docs JSON | `<BASE_URL>/v3/api-docs` |

---

## 3. 공통 응답 형식

모든 API 응답은 아래 형식을 사용합니다.

```json
{
  "success": true,
  "code": 200,
  "message": "응답 메시지",
  "data": {}
}
```

| 필드 | 타입 | 설명 |
|---|---|---|
| `success` | boolean | 요청 처리 성공 여부 |
| `code` | number | HTTP 상태 코드 |
| `message` | string | 사용자/클라이언트 표시용 메시지 |
| `data` | object / array / null | 실제 응답 데이터 |

### 3.1 성공 응답 예시

```json
{
  "success": true,
  "code": 200,
  "message": "로그인에 성공했습니다.",
  "data": {
    "userId": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "accessToken": "<JWT_ACCESS_TOKEN>",
    "refreshToken": "<JWT_REFRESH_TOKEN>",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "refreshExpiresIn": 1209600000
  }
}
```

### 3.2 실패 응답 예시

```json
{
  "success": false,
  "code": 401,
  "message": "인증이 필요합니다.",
  "data": null
}
```

### 3.3 `data` 형태별 예시

`data = null`
```json
{
  "success": true,
  "code": 200,
  "message": "로그아웃에 성공했습니다.",
  "data": null
}
```

`data = object`
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

`data = array` (공통 포맷 예시)
```json
{
  "success": true,
  "code": 200,
  "message": "요청을 성공적으로 처리했습니다.",
  "data": [
    {
      "id": 1,
      "name": "example"
    }
  ]
}
```

---

## 4. 공통 에러 형식

### 4.1 400 Validation Error
```json
{
  "success": false,
  "code": 400,
  "message": "email: 이메일 형식이 올바르지 않습니다.",
  "data": null
}
```

### 4.2 401 Unauthorized
```json
{
  "success": false,
  "code": 401,
  "message": "인증이 필요합니다.",
  "data": null
}
```

### 4.3 403 Forbidden
```json
{
  "success": false,
  "code": 403,
  "message": "접근 권한이 없습니다.",
  "data": null
}
```

### 4.4 404 Not Found
```json
{
  "success": false,
  "code": 404,
  "message": "사용자를 찾을 수 없습니다.",
  "data": null
}
```

### 4.5 409 Conflict
```json
{
  "success": false,
  "code": 409,
  "message": "이미 사용 중인 이메일입니다.",
  "data": null
}
```

### 4.6 500 Internal Server Error
```json
{
  "success": false,
  "code": 500,
  "message": "서버 내부 오류가 발생했습니다.",
  "data": null
}
```

---

## 5. 인증 방식

### 5.1 JWT Access Token
- 로그인/재발급 성공 시 발급
- 보호된 API 호출 시 `Authorization` 헤더로 전달

### 5.2 Refresh Token
- 로그인/재발급 성공 시 발급
- `POST /api/auth/reissue` 요청 body에 포함해서 재발급 요청
- 서버 DB(`refresh_tokens`)에 저장/갱신됨

### 5.3 Authorization Header 형식
```http
Authorization: Bearer <ACCESS_TOKEN>
```

- `Bearer` 접두어가 반드시 필요합니다.
- 접두어가 없거나 형식이 다르면 `401` + `"Bearer 토큰 형식이 아닙니다."` 응답이 발생할 수 있습니다.

### 5.4 Access Token 만료 시 처리
1. 보호 API에서 `401` 발생
2. 프론트에서 보관 중인 `refreshToken`으로 `POST /api/auth/reissue` 호출
3. 재발급 성공 시 새 `accessToken`, `refreshToken`으로 교체 후 원요청 재시도

### 5.5 로그아웃 시 처리
- `POST /api/auth/logout` 호출 시 DB의 해당 사용자 Refresh Token 삭제
- 프론트는 로컬 저장소의 `accessToken`, `refreshToken`도 즉시 삭제해야 함

---

## 6. API 목록

| Method | URL | 인증 필요 | 설명 |
|---|---|---|---|
| POST | `/api/auth/signup` | 아니오 | 회원가입 |
| POST | `/api/auth/login` | 아니오 | 로그인(Access/Refresh 발급) |
| POST | `/api/auth/reissue` | 아니오 | Refresh Token으로 Access/Refresh 재발급 |
| POST | `/api/auth/logout` | 예 | 로그아웃(Refresh Token 삭제) |
| GET | `/api/users/me` | 예 | 내 정보 조회 |

---

## 7. 회원가입 API

### 7.1 기본 정보
- **Method / URL**: `POST /api/auth/signup`
- **인증 필요 여부**: 불필요
- **설명**: 이메일/비밀번호/이름으로 신규 사용자 생성

### 7.2 Request Headers
| Header | 값 |
|---|---|
| `Content-Type` | `application/json` |

### 7.3 Request Body

| 필드 | 타입 | 필수 | 규칙 |
|---|---|---|---|
| `email` | string | 예 | `NotBlank`, `Email` |
| `password` | string | 예 | `NotBlank`, 최소 8자 |
| `name` | string | 예 | `NotBlank` |

### 7.4 Validation 규칙 예시
- `email` 누락: `email: 이메일은 필수입니다.`
- `email` 형식 오류: `email: 이메일 형식이 올바르지 않습니다.`
- `password` 길이 부족: `password: 비밀번호는 8자 이상이어야 합니다.`

### 7.5 Request 예시
```json
{
  "email": "test@example.com",
  "password": "password1234",
  "name": "홍길동"
}
```

### 7.6 Success Response 예시
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

### 7.7 Error Response 예시
- 중복 이메일(409)
```json
{
  "success": false,
  "code": 409,
  "message": "이미 사용 중인 이메일입니다.",
  "data": null
}
```

### 7.8 curl 예시
```bash
curl -X POST "$BASE_URL/api/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password1234","name":"홍길동"}'
```

### 7.9 프론트엔드 참고사항
- 회원가입 직후 자동 로그인은 서버에서 수행하지 않습니다.
- 회원가입 성공 후 별도로 로그인 API를 호출해 토큰을 발급받아야 합니다.

---

## 8. 로그인 API

### 8.1 기본 정보
- **Method / URL**: `POST /api/auth/login`
- **인증 필요 여부**: 불필요
- **설명**: 로그인 성공 시 Access Token + Refresh Token 발급

### 8.2 Request Body
```json
{
  "email": "test@example.com",
  "password": "password1234"
}
```

### 8.3 Success Response 예시
```json
{
  "success": true,
  "code": 200,
  "message": "로그인에 성공했습니다.",
  "data": {
    "userId": 1,
    "email": "test@example.com",
    "name": "홍길동",
    "accessToken": "<JWT_ACCESS_TOKEN>",
    "refreshToken": "<JWT_REFRESH_TOKEN>",
    "tokenType": "Bearer",
    "expiresIn": 3600000,
    "refreshExpiresIn": 1209600000
  }
}
```

### 8.4 실패 케이스
- 존재하지 않는 이메일: `404`, `"사용자를 찾을 수 없습니다."`
- 비밀번호 불일치: `400`, `"비밀번호가 올바르지 않습니다."`
- Validation 실패: `400`, 예: `"email: 이메일 형식이 올바르지 않습니다."`

### 8.5 curl 예시
```bash
curl -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password1234"}'
```

### 8.6 프론트 저장 권장 값
- `data.accessToken`
- `data.refreshToken`
- `data.expiresIn`
- `data.refreshExpiresIn`
- 필요 시 `data.userId`, `data.email`, `data.name`

---

## 9. 토큰 재발급 API

### 9.1 기본 정보
- **Method / URL**: `POST /api/auth/reissue`
- **인증 필요 여부**: 불필요
- **설명**: Refresh Token으로 Access/Refresh 토큰 재발급

### 9.2 Request Body
```json
{
  "refreshToken": "<JWT_REFRESH_TOKEN>"
}
```

### 9.3 Success Response 예시
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

### 9.4 실패 케이스
- Refresh Token 누락/빈값: `400`, `"refreshToken: 리프레시 토큰은 필수입니다."`
- 잘못된 Refresh Token 형식: `401`, `"잘못된 JWT 형식입니다."` 또는 `"유효하지 않은 토큰입니다."`
- 만료된 Refresh Token: `401`, `"만료된 토큰입니다."` 또는 `"만료된 리프레시 토큰입니다."`
- DB에 존재하지 않는 Refresh Token: `404`, `"리프레시 토큰을 찾을 수 없습니다."`

### 9.5 curl 예시
```bash
curl -X POST "$BASE_URL/api/auth/reissue" \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<JWT_REFRESH_TOKEN>"}'
```

### 9.6 프론트 자동 재발급 흐름
1. 보호 API 응답이 `401`이면 재발급 API 호출
2. 성공 시 새 토큰 저장
3. 실패 시 강제 로그아웃 처리(토큰 삭제 + 로그인 페이지 이동)

---

## 10. 로그아웃 API

### 10.1 기본 정보
- **Method / URL**: `POST /api/auth/logout`
- **인증 필요 여부**: 필요
- **Authorization Header**: 필수
- **설명**: 현재 사용자 기준 Refresh Token 삭제

### 10.2 Success Response 예시
```json
{
  "success": true,
  "code": 200,
  "message": "로그아웃에 성공했습니다.",
  "data": null
}
```

### 10.3 실패 케이스
- 토큰 없음: `401`, `"인증이 필요합니다."`
- 토큰 형식 오류: `401`, `"Bearer 토큰 형식이 아닙니다."`
- 토큰 만료/무효: `401`, `"만료된 토큰입니다."` / `"잘못된 JWT 형식입니다."`
- 사용자 없음: `404`, `"사용자를 찾을 수 없습니다."`

### 10.4 curl 예시
```bash
curl -X POST "$BASE_URL/api/auth/logout" \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>"
```

### 10.5 프론트엔드 참고사항
- 로그아웃 API 성공 시점에 클라이언트 토큰도 즉시 삭제
- 로그아웃 API 실패(401/404)여도 로컬 토큰 제거는 수행하는 것이 안전

---

## 11. 내 정보 조회 API

### 11.1 기본 정보
- **Method / URL**: `GET /api/users/me`
- **인증 필요 여부**: 필요
- **Authorization Header**: 필수

### 11.2 Success Response 예시
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

`password`는 응답에 포함되지 않습니다. (`UserResponse` 필드에 없음)

### 11.3 실패 케이스
- 토큰 없음: `401`, `"인증이 필요합니다."`
- 토큰 만료: `401`, `"만료된 토큰입니다."`
- 잘못된 토큰: `401`, `"잘못된 JWT 형식입니다."` 등
- 사용자 없음: `404`, `"사용자를 찾을 수 없습니다."`

### 11.4 curl 예시
```bash
curl -X GET "$BASE_URL/api/users/me" \
  -H "Authorization: Bearer <JWT_ACCESS_TOKEN>"
```

---

## 12. Swagger UI 사용법

1. Swagger UI 접속: `<BASE_URL>/swagger-ui.html`
2. 우측 상단 `Authorize` 클릭
3. Security Scheme 이름: `Bearer Authentication`
4. 토큰 입력 후 `Authorize`
   - 입력 값 예시: `<JWT_ACCESS_TOKEN>`
   - 요청 헤더에는 `Authorization: Bearer <JWT_ACCESS_TOKEN>` 형태로 전달됨
5. 인증이 필요한 API(`logout`, `/api/users/me`)를 실행해서 응답 확인

---

## 13. 프론트엔드 연동 가이드

1. 로그인 성공 후 `accessToken`, `refreshToken` 저장
2. 보호 API 호출 시 `Authorization` 헤더 추가
3. `401` 응답 시 `/api/auth/reissue` 자동 호출
4. 재발급 성공 시 토큰 갱신 후 기존 요청 재시도
5. 재발급 실패 시 로그아웃 처리(토큰 삭제 + 로그인 화면 이동)
6. `403` 응답 시 권한 없음 화면/메시지 처리
7. Validation 에러(`400`)는 `message`를 그대로 폼 에러로 표시

---

## 14. 테스트용 요청 순서

1. `POST /api/auth/signup` 회원가입
2. `POST /api/auth/login` 로그인(토큰 획득)
3. Swagger `Authorize`에 Access Token 설정
4. `GET /api/users/me` 호출
5. `POST /api/auth/reissue` 호출(Refresh Token으로 재발급 확인)
6. `POST /api/auth/logout` 호출

---

## 15. 주의사항

1. `application.yml`의 DB 비밀번호, JWT secret 등 민감정보는 문서/코드 저장소에 직접 노출하지 않습니다.
2. 운영 환경에서는 DB/JWT 설정을 환경변수 또는 서버 전용 secret 관리 방식으로 분리하는 것이 안전합니다.
3. Docker 환경에서 DB 연결 host는 `localhost`가 아니라 compose service name(`mysql`)을 사용합니다.

