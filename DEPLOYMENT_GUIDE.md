# KYJ 코어 라이브러리 배포 가이드

## 프로젝트 구조

```
kyj-fk-be-core/
├── kyj-lib-core/           # 필수 코어 모듈 (항상 포함)
├── kyj-lib-core-kafka/     # 카프카 모듈 (선택적)
├── kyj-lib-core-redis/     # 레디스 모듈 (선택적)
├── kyj-lib-core-file/      # 파일(S3) 모듈 (선택적)
├── kyj-lib-core-jpa/       # JPA 모듈 (선택적)
├── kyj-lib-core-rdb/       # RDB 모듈 (선택적)
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

# 코어 + 파일 모듈 배포
./gradlew publishWithFile

# 코어 + JPA 모듈 배포
./gradlew publishWithJpa

# 코어 + RDB 모듈 배포
./gradlew publishWithRdb

# 모든 모듈 배포
./gradlew publishAll
```

### 2. 직접 모듈 지정 배포

특정 모듈들만 선택해서 배포:

```bash
# 특정 모듈 배포
./gradlew :kyj-lib-core:publish
./gradlew :kyj-lib-core-kafka:publish :kyj-lib-core-redis:publish

# 여러 모듈 동시 배포
./gradlew :kyj-lib-core:publish :kyj-lib-core-kafka:publish :kyj-lib-core-rdb:publish
```

## 로컬 개발 환경 설정

각 서브 모듈(kafka, redis, file, jpa, rdb)에서 코어 모듈을 참조해야 할 때는 `localDev` 플래그를 사용합니다.

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

### 3. 데이터베이스 관련 모듈과 함께 사용

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-jpa:0.0.1-SNAPSHOT'      // JPA 사용시
    implementation 'com.kyj:kyj-lib-core-rdb:0.0.1-SNAPSHOT'      // RDB 사용시
}
```

### 4. 여러 모듈과 함께 사용

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-kafka:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-redis:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-file:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-jpa:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-rdb:0.0.1-SNAPSHOT'
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

### kyj-lib-core-jpa
- JPA 설정 및 QueryDSL 지원
- 데이터베이스 관련 유틸리티

### kyj-lib-core-rdb
- JDBC 기반 데이터베이스 연결
- 다중 데이터소스 지원
- 트랜잭션 관리
- 데이터베이스 락 기능

## 주의사항

1. **코어 모듈은 필수**: 다른 모듈들을 사용할 때는 항상 `kyj-lib-core`도 함께 포함해야 합니다.

2. **로컬 개발시**: 서브 모듈에서 코어 기능을 참조해야 할 때는 `-PlocalDev=true` 플래그를 사용합니다.

3. **배포시**: 서브 모듈들은 독립적으로 배포되며, 실제 사용할 때는 필요한 모듈들을 조합하여 사용합니다.

4. **버전 관리**: 모든 모듈은 동일한 버전으로 관리되므로, 호환성을 위해 같은 버전의 모듈들을 사용하는 것을 권장합니다.

5. **JPA vs RDB**: JPA 모듈은 ORM 기반, RDB 모듈은 JDBC 기반이므로 프로젝트 성격에 맞게 선택하여 사용하세요.