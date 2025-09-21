# KYJ 코어 라이브러리 배포 가이드

## 프로젝트 구조

```
kyj-fk-be-core/
├── kyj-lib-core/           # 필수 코어 모듈 (항상 포함)
├── kyj-lib-core-kafka/     # 카프카 모듈 (선택적)
├── kyj-lib-core-redis/     # 레디스 모듈 (선택적)
├── kyj-lib-core-file/      # 파일(S3) 모듈 (선택적)
├── kyj-lib-core-jpa/       # JPA 모듈 (선택적)
└── kyj-lib-redis/          # 레거시 모듈 (정리 예정)
```

## 배포 방법

### 1. 개별 모듈 배포

각 모듈을 개별적으로 넥서스에 배포할 수 있습니다.

```bash
# 코어 모듈만 배포
./gradlew publishCore

# 코어 + 카프카 모듈 배포
./gradlew publishWithKafka

# 코어 + 레디스 모듈 배포
./gradlew publishWithRedis



# 모든 모듈 배포
./gradlew publishAll
```

### 2. 직접 모듈 지정 배포

특정 모듈들만 선택해서 배포:



# 여러 모듈 동시 배포
./gradlew :kyj-lib-core:publish :kyj-lib-core-kafka:publish :kyj-lib-core-redis:publish
```

## 로컬 개발 환경 설정

각 서브 모듈(kafka, redis, file, jpa)에서 코어 모듈을 참조해야 할 때는 `localDev` 플래그를 사용합니다.

```bash
# 로컬 개발용 빌드 (코어 모듈 참조 포함)
./gradlew build -PlocalDev=true

# 로컬 개발용 테스트
./gradlew test -PlocalDev=true

# 로컬 개발용 IDE 설정 생성
./gradlew idea -PlocalDev=true
./gradlew eclipse -PlocalDev=true
```

## 실제 프로젝트에서의 사용

### 1. 기본 사용 (코어만)

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
}
```

### 2. 카프카와 함께 사용

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-kafka:0.0.1-SNAPSHOT'
}
```

### 3. 여러 모듈과 함께 사용

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-kafka:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-redis:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-jpa:0.0.1-SNAPSHOT'
}
```

## 모듈별 기능

### kyj-lib-core (필수)
- 스프링 부트 기본 설정 (Web, AOP, Validation, Mail 등)
- 예외 처리 및 로깅
- 유틸리티 클래스들
- 모니터링 (Actuator, Prometheus)

### kyj-lib-core-kafka
- 카프카 관련 설정 및 유틸리티
- 메시지 생산/소비 기능

### kyj-lib-core-redis
- 레디스 연결 및 설정
- 캐싱 관련 기능

### kyj-lib-core-file
- AWS S3 파일 업로드/다운로드
- 파일 관리 기능


## 주의사항

1. **코어 모듈은 필수**: 다른 모듈들을 사용할 때는 항상 `kyj-lib-core`도 함께 포함해야 합니다.

2. **로컬 개발시**: 서브 모듈에서 코어 기능을 참조해야 할 때는 `-PlocalDev=true` 플래그를 사용합니다.

3. **배포시**: 서브 모듈들은 독립적으로 배포되며, 실제 사용할 때는 필요한 모듈들을 조합하여 사용합니다.
