# MySQL Setup

애플리케이션 실행 전에 MySQL 서버가 **반드시 실행 중**이어야 합니다.

## 1) DB 생성 SQL

```sql
CREATE DATABASE final_study_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

필요하면 접속 계정 권한도 함께 확인하세요.

```sql
-- 예시: root 계정 전체 권한 (로컬 개발 환경 전용)
GRANT ALL PRIVILEGES ON final_study_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

## 2) application.yml 확인

`src/main/resources/application.yml`에서 아래 항목을 프로젝트 환경에 맞게 확인/수정하세요.

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret-key`
- `jwt.access-token.expiration`
- `jwt.refresh-token.expiration`

## 3) 보안 안내

현재 설정은 학습/개발 편의를 위해 민감정보(DB 비밀번호, JWT 시크릿)를 파일에 직접 작성합니다.
실서비스 배포 시에는 반드시 환경변수 또는 시크릿 매니저로 분리하세요.

