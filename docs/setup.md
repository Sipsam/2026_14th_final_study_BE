# Local Setup Guide

## 1) MySQL DB 생성
```sql
CREATE DATABASE final_study_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

필요 시 권한 부여:
```sql
GRANT ALL PRIVILEGES ON final_study_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

## 2) application.yml 설정
파일: `src/main/resources/application.yml`

필수 확인 항목:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `jwt.secret-key`
- `jwt.access-token.expiration`
- `jwt.refresh-token.expiration`

예시:
- `spring.datasource.url=jdbc:mysql://localhost:3306/final_study_db?...`
- `spring.datasource.username=root`
- `spring.datasource.password=your-local-mysql-password`
- `jwt.secret-key=your-jwt-secret-key-placeholder`

중요:
- 현재 로컬 개발용 `application.yml`은 환경변수 없이 직접 값을 작성하는 방식입니다.
- 실제 운영 환경에서는 민감정보(DB 비밀번호, JWT secret)를 환경변수 또는 서버 전용 설정 파일로 분리하세요.

## 3) IntelliJ 실행 방법
1. IntelliJ에서 프로젝트 오픈
2. Gradle Sync 완료 확인
3. Run Configuration에서 `FinalStudyApplication` 실행  
   (main class: `com.likelion.finalstudy.FinalStudyApplication`)
4. 또는 터미널에서 실행

```bash
./gradlew bootRun
```
