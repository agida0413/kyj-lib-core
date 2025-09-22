# KYJ Core Library

Spring Boot 기반  멀티모듈 라이브러리

마이크로서비스 아키텍처를 위한 인증/인가, 분산 처리, 데이터 관리 등 핵심 기능들을 모듈화하여 제공

## 🏗️ 아키텍처 개요

8개의 독립적인 모듈로 구성되어 있으며, 필요에 따라 선택적으로 사용할 수 있습니다.

```
kyj-fk-be-core/
├── kyj-lib-core/                    # 🔧 필수 코어 모듈
├── kyj-lib-core-redis/              # 🔴 Redis & 분산락
├── kyj-lib-core-kafka/              # 📨 Kafka 메시징
├── kyj-lib-core-file/               # 📁 AWS S3 파일관리
├── kyj-lib-core-jpa/                # 🗄️ JPA & QueryDSL
├── kyj-lib-core-rdb/                # 🔀 RDB 트랜잭션 관리
├── kyj-lib-core-security-auth/      # 🔐 OAuth2 인증서버
├── kyj-lib-core-security-client/    # 🛡️ JWT 토큰 검증

```

## 📦 모듈별 상세 기능

### 🔧 kyj-lib-core (필수 기반 모듈)

**주요 기능:**
- 표준화된 API 응답 포맷 (`ResApiDTO`, `ResApiErrDTO`)
- 글로벌 예외 처리 (`ExceptionAdvisor`)
- 비동기 처리 및 스레드풀 관리
- AOP 기반 로깅 및 메트릭스
- 이메일 발송 유틸리티
- 지리적 계산, 쿠키 관리 등 유틸리티

### 🔴 kyj-lib-core-redis (캐싱 & 분산 락)

**주요 기능:**
- Redis 연결 및 설정 자동화
- `@DtbLock` 어노테이션 기반 분산 락
- Spring Cache 추상화 지원
- 세션 관리 및 캐시 전략

### 📨 kyj-lib-core-kafka (이벤트 메시징)

**주요 기능:**


### 📁 kyj-lib-core-file (AWS S3 파일 관리)

**주요 기능:**
- AWS S3 클라이언트 자동 설정
- 파일 업로드/다운로드 서비스
- 파일 타입 검증 및 보안
- 멀티파트 파일 처리

### 🗄️ kyj-lib-core-jpa (ORM & Type-Safe Query)

**주요 기능:**


### 🔀 kyj-lib-core-rdb (트랜잭션 & 데이터소스 관리)

**주요 기능:**
- 읽기/쓰기 데이터소스 분리
- `@ReadOnlyDB`, `@WriteDB` 어노테이션
- AOP 기반 트랜잭션 관리
- 동적 데이터소스 라우팅

### 🔐 kyj-lib-core-security-auth (OAuth2 인증 서버)

**주요 기능:**
- Google, Naver, Kakao 소셜 로그인
- JWT Access/Refresh 토큰 생성
- Redis 기반 토큰 저장소
- 커스터마이징 가능한 인증 로직
- 토큰 갱신 및 로그아웃 처리

### 🛡️ kyj-lib-core-security-client (토큰 검증 & 인가)

**주요 기능:**
- JWT 토큰 검증 및 사용자 정보 추출
- API Gateway 모드 지원 (헤더 기반 인증)
- `@PublicEndpoint`, `@RequireRole` 어노테이션
- Redis 기반 토큰 블랙리스트(커스터마이징 가능)
- 현재 사용자 컨텍스트 관리


## 🎯 사용 시나리오

### 마이크로서비스 환경
```gradle
// API Gateway (인증 서버)
dependencies {
    implementation 'com.kyj:kyj-lib-core'
    implementation 'com.kyj:kyj-lib-core-redis'
    implementation 'com.kyj:kyj-lib-core-kafka'
    implementation 'com.kyj:kyj-lib-core-security-auth'
}

// 일반 서비스 (토큰 검증)
dependencies {
    implementation 'com.kyj:kyj-lib-core'
    implementation 'com.kyj:kyj-lib-core-redis'
    implementation 'com.kyj:kyj-lib-core-security-client'
}
```



## ⚙️ 설정

### application.yml 예시
```yaml
kyj:
  security:
    auth:
      token:
        secret: ${JWT_SECRET}
        access-token-expiry: 3600000
        refresh-token-expiry: 86400000
      oauth2:
        success-redirect-url: ${FRONTEND_URL}/auth/success
    client:
      enabled: true
      use-api-gateway: false  # true: API Gateway 모드
      enable-blacklist: true
      api-gateway:
        user-id-header: "X-User-Id"
        username-header: "X-Username"

spring:
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS}
```

## 🚀 배포 전략

### 모듈별 선택적 배포
```bash
# 기본 웹 서비스
./gradlew publishCore

# 캐싱이 필요한 서비스
./gradlew publishWithRedis

# 인증이 필요한 서비스
./gradlew publishWithSecurityClient

# 인증 서버
./gradlew publishWithSecurityAuth

# 모든 모듈
./gradlew publishAll
```



