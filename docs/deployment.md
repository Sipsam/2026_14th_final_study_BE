# Vultr Docker Compose Deployment (CD)

## 개요

- GitHub Actions 워크플로우: `.github/workflows/deploy.yml`
- 트리거: `week8/#6-jangsungwon` 브랜치 `push`
- 배포 방식: Vultr Ubuntu 서버에 SSH 접속 후 `docker compose` 재배포

## 사전 준비

1. Vultr Ubuntu 서버에 Docker / Docker Compose 설치
2. 서버에 프로젝트 저장소 1회 클론
3. 서버에서 배포 브랜치(`week8/#6-jangsungwon`) 접근 가능 상태 확인

기본 워크플로우는 서버 경로를 `~/<repository-name>`으로 가정합니다.

## GitHub Secrets

Repository Settings > Secrets and variables > Actions > New repository secret 에 아래 3개를 등록합니다.

- `SSH_HOST`: Vultr 서버 IP 또는 도메인
- `SSH_USERNAME`: SSH 사용자명 (예: `root`, `ubuntu`)
- `SSH_PRIVATE_KEY`: 해당 사용자로 접속 가능한 개인키 전체 내용

## 배포 동작 순서

워크플로우는 서버에서 아래 순서로 실행됩니다.

1. `cd ~/<repository-name>`
2. `git pull origin week8/#6-jangsungwon`
3. `docker compose down`
4. `docker compose up -d --build`
5. `docker ps`
6. Health check:
   - 우선 `http://localhost:8080/actuator/health`
   - 실패 시 `http://localhost:8080/v3/api-docs` fallback

## Docker 프로파일/DB 설정

- `docker-compose.yml`에서 앱 컨테이너는 `SPRING_PROFILES_ACTIVE=docker`를 사용합니다.
- 따라서 Docker 배포 시 `application-docker.yml`이 적용됩니다.
- DB URL은 아래 형식을 사용합니다.
  - `jdbc:mysql://mysql:3306/final_study_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true`

`application.yml`의 로컬 MySQL 설정은 로컬 실행용이며, 배포에서는 사용되지 않습니다.
