# 2026 14th Final Study BE

백엔드 과제 저장소입니다.

## 로컬 실행

- 로컬 프로파일(`application.yml`)은 MySQL 기준 설정입니다.
- DB 생성 및 설정 가이드는 `docs/setup.md`를 확인하세요.

## GitHub Actions CI

- 워크플로우 파일: `.github/workflows/ci.yml`
- 실행 트리거: `week8/#6-jangsungwon` 브랜치의 `push`, `pull_request`
- 실행 내용: Java 25 설정, Gradle 캐시 사용, `./gradlew clean build` 실행
- 테스트 DB: `test` 프로파일(H2 메모리 DB) 기반으로 외부 MySQL 없이 테스트 수행
