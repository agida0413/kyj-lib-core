# Jenkins 파이프라인 사용 가이드

## 📋 개요

이 Jenkins 파이프라인은 KYJ 코어 라이브러리의 모듈별 선택적 배포를 지원합니다. 필요한 모듈만 선택하여 넥서스에 배포할 수 있습니다.

## 🚀 주요 기능

### 1. 모듈별 선택적 배포
- **CORE_ONLY**: 코어 모듈만 배포
- **WITH_KAFKA**: 코어 + 카프카 모듈 배포
- **WITH_REDIS**: 코어 + 레디스 모듈 배포
- **WITH_FILE**: 코어 + 파일(S3) 모듈 배포
- **WITH_JPA**: 코어 + JPA 모듈 배포
- **WITH_RDB**: 코어 + RDB 모듈 배포
- **ALL_MODULES**: 모든 모듈 배포

### 2. 브랜치 선택
- main
- feature/refactoring-2025/09
- develop

### 3. 빌드 옵션
- **RUN_TESTS**: 테스트 실행 여부 (기본값: true)
- **CLEAN_BUILD**: Clean 빌드 실행 여부 (기본값: true)

## 🔧 Jenkins 설정 요구사항

### 필수 도구 설정
```groovy
tools {
    gradle 'gradle-8.3'  // Jenkins에서 Gradle 8.3이 설정되어 있어야 함
    jdk 'jdk-17'        // Jenkins에서 JDK 17이 설정되어 있어야 함
}
```

### 환경 변수
- `APP_GITHUB_URL`: GitHub 저장소 URL
- `APP_VERSION`: 애플리케이션 버전 (0.0.1-SNAPSHOT)
- `GRADLE_OPTS`: Gradle 옵션 (-Dorg.gradle.daemon=false)

## 📝 파이프라인 단계

### 1. 환경 정보 출력
선택된 배포 옵션들을 확인하고 출력합니다.

### 2. 소스코드 체크아웃
선택된 브랜치에서 소스코드를 가져옵니다.

### 3. Gradle 권한 설정
gradlew 파일에 실행 권한을 부여합니다.

### 4. 소스 빌드
선택된 옵션에 따라 빌드를 수행합니다:
```bash
# Clean 빌드 + 테스트 실행
./gradlew clean build

# Clean 빌드 + 테스트 제외
./gradlew clean build -x test

# 일반 빌드 + 테스트 제외
./gradlew build -x test
```

### 5. 모듈별 넥서스 배포
선택된 배포 타입에 따라 해당 모듈들을 넥서스에 배포합니다:
```bash
./gradlew publishCore           # 코어만
./gradlew publishWithKafka     # 코어 + 카프카
./gradlew publishWithRedis     # 코어 + 레디스
./gradlew publishWithFile      # 코어 + 파일
./gradlew publishWithJpa       # 코어 + JPA
./gradlew publishWithRdb       # 코어 + RDB
./gradlew publishAll           # 모든 모듈
```

### 6. 배포 결과 확인
배포 완료 정보와 의존성 정보를 출력합니다.

## 📊 Post 액션

### Always
- 파이프라인 실행 정보 출력
- JAR 파일 아카이브 (build/libs/*.jar)
- 테스트 결과 퍼블리시

### Success/Failure/Unstable
각 상황에 맞는 메시지를 출력합니다.

## 🎯 사용 예시

### 시나리오 1: 개발용 코어 모듈만 배포
```
DEPLOY_TYPE: CORE_ONLY
BRANCH: develop
RUN_TESTS: true
CLEAN_BUILD: true
```

### 시나리오 2: 프로덕션용 카프카 포함 배포
```
DEPLOY_TYPE: WITH_KAFKA
BRANCH: main
RUN_TESTS: true
CLEAN_BUILD: true
```

### 시나리오 3: 데이터베이스 관련 모듈 배포
```
DEPLOY_TYPE: WITH_RDB
BRANCH: main
RUN_TESTS: true
CLEAN_BUILD: true
```

### 시나리오 4: 빠른 배포 (테스트 제외)
```
DEPLOY_TYPE: ALL_MODULES
BRANCH: feature/refactoring-2025/09
RUN_TESTS: false
CLEAN_BUILD: false
```


## 🔍 트러블슈팅

### 문제 1: Gradle 권한 오류
```
해결방법: Gradle 권한 설정 단계에서 chmod +x ./gradlew가 정상 실행되는지 확인
```

### 문제 2: 넥서스 연결 실패
```
해결방법: VPN 연결 상태 확인 및 넥서스 서버 접근 가능 여부 확인
```

### 문제 3: 빌드 실패
```
해결방법: 선택한 브랜치의 코드 상태 확인 및 의존성 문제 해결
```
