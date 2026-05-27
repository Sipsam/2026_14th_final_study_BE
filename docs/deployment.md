# Deployment Guide

## 1) Docker Compose 배포
프로젝트 루트에서 실행:

```bash
docker compose down
docker compose up -d --build
docker compose ps
```

`docker-compose.yml`에서 앱은 `SPRING_PROFILES_ACTIVE=docker`를 사용하므로 `application-docker.yml`이 적용됩니다.

DB URL:
- `jdbc:mysql://mysql:3306/final_study_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true`

## 2) GitHub Actions

### CI
- 파일: `.github/workflows/ci.yml`
- 트리거: `week8/#6-jangsungwon` 브랜치 push/pull_request
- 동작: Java 25 + Gradle cache + `./gradlew clean build` (test profile/H2)

### CD
- 파일: `.github/workflows/deploy.yml`
- 트리거: `week8/#6-jangsungwon` 브랜치 push
- 동작: Vultr 서버 SSH 접속 후 `git pull`, `docker compose down`, `docker compose up -d --build`, health check

## 3) Vultr 배포 방법

### 사전 준비
1. Vultr Ubuntu 서버에 Docker / Docker Compose 설치
2. 서버에 프로젝트를 1회 clone
3. 서버에서 배포 브랜치 접근 가능 상태 확인

### GitHub Secrets
Repository Settings > Secrets and variables > Actions:
- `SSH_HOST`
- `SSH_USERNAME`
- `SSH_PRIVATE_KEY`

### 배포 흐름
`deploy.yml`이 서버에서 아래 명령을 실행합니다.
1. `cd ~/<repository-name>`
2. `git pull origin week8/#6-jangsungwon`
3. `docker compose down`
4. `docker compose up -d --build`
5. `docker ps`
6. `http://localhost:8080/actuator/health` 확인  
   (실패 시 `http://localhost:8080/v3/api-docs` fallback)
