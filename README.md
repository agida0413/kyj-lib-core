# KYJ Core Library

Spring Boot 기반 멀티모듈 코어 라이브러리입니다. 마이크로서비스 아키텍처와 OAuth2 기반 인증/인가 시스템을 위한 재사용 가능한 컴포넌트들을 제공합니다.

## 📋 프로젝트 구조

```
kyj-fk-be-core/
├── kyj-lib-core/                   # 필수 코어 모듈 (항상 포함)
├── kyj-lib-core-kafka/             # 카프카 모듈 (선택적)
├── kyj-lib-core-redis/             # 레디스 모듈 (선택적)
├── kyj-lib-core-file/              # 파일(S3) 모듈 (선택적)
├── kyj-lib-core-jpa/               # JPA 모듈 (선택적)
├── kyj-lib-core-rdb/               # RDB 모듈 (선택적)
├── kyj-lib-core-security-client/   # 시큐리티 클라이언트 모듈 (선택적)
├── kyj-lib-core-security-auth/     # 시큐리티 인증서버 모듈 (선택적)
└── kyj-lib-redis/                  # 레거시 모듈 (정리 예정)
```

## 🚀 주요 기능

### kyj-lib-core (필수)
- 스프링 부트 기본 설정 (Web, AOP, Validation, Mail 등)
- 통일된 API 응답 포맷 (ResApiDTO, ResApiErrDTO)
- 예외 처리 및 로깅
- 유틸리티 클래스들
- 모니터링 (Actuator, Prometheus)

### kyj-lib-core-kafka
- 카프카 관련 설정 및 유틸리티
- 메시지 생산/소비 기능

### kyj-lib-core-redis
- 레디스 연결 및 설정
- 캐싱 관련 기능
- 분산 락 기능

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

### kyj-lib-core-security-client
- JWT 토큰 검증 및 인증/인가 처리
- AWS API Gateway에서 검증된 JWT 토큰 처리
- 토큰에서 사용자 정보 추출 (사용자 ID, 이메일, 권한 등)
- Redis 기반 토큰 블랙리스트 검증
- MSA 서비스에서 쉽게 사용할 수 있는 유틸리티 제공
- 현재 요청의 사용자 정보 조회 기능

### kyj-lib-core-security-auth
- OAuth2 + JWT 기반 인증/인가 서버 프레임워크
- Google, Naver, Kakao 등 소셜 로그인 지원
- JWT 토큰 생성 및 관리
- 커스터마이징 가능한 인증/인가 로직
- 각 사이드프로젝트에서 재사용 가능한 구조

## 📦 사용법

### 기본 사용 (코어만)

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
}
```

### 카프카와 함께 사용

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-kafka:0.0.1-SNAPSHOT'
}
```

### 데이터베이스 관련 모듈과 함께 사용

```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-jpa:0.0.1-SNAPSHOT'      // JPA 사용시
    implementation 'com.kyj:kyj-lib-core-rdb:0.0.1-SNAPSHOT'      // RDB 사용시
}
```

### MSA 서비스에서 인증 확인용 (클라이언트)
```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-redis:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-security-client:0.0.1-SNAPSHOT'
}
```

### 인증/인가 서버 구축용
```gradle
dependencies {
    implementation 'com.kyj:kyj-lib-core:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-redis:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-kafka:0.0.1-SNAPSHOT'
    implementation 'com.kyj:kyj-lib-core-security-auth:0.0.1-SNAPSHOT'
}
```

## 🔐 Security 모듈 사용 가이드

### kyj-lib-core-security-auth (OAuth2 인증 서버)

OAuth2 기반 인증 서버 라이브러리로 Google, Naver, Kakao 소셜 로그인을 지원합니다.

#### 빠른 시작

1. **AuthMemberService 구현**

```java
@Service
public class MemberServiceImpl implements AuthMemberService {

    @Override
    public AuthMemberDTO findOrCreateMember(OAuth2Response oAuth2Response) {
        String email = oAuth2Response.getEmail();
        Member member = memberRepository.findByEmail(email)
            .orElseGet(() -> createNewMember(oAuth2Response));
        return convertToDTO(member);
    }

    @Override
    public AuthMemberDTO findMemberByUserId(String userId) {
        return memberRepository.findById(Long.valueOf(userId))
            .map(this::convertToDTO)
            .orElse(null);
    }

    @Override
    public boolean isMemberActive(AuthMemberDTO memberDTO) {
        return memberDTO.isActive();
    }
}
```

2. **주요 엔드포인트**
- Google: `/oauth2/authorization/google`
- Naver: `/oauth2/authorization/naver`
- Kakao: `/oauth2/authorization/kakao`
- 토큰 갱신: `POST /auth/reissue`
- 로그아웃: `POST /auth/logout`

### kyj-lib-core-security-client (JWT 토큰 검증)

마이크로서비스에서 인증 서버의 JWT 토큰을 검증하고 사용자 정보를 추출합니다.

#### 인증 정책

기본적으로 모든 엔드포인트에서 인증이 필요하며, `@PublicEndpoint`로 예외 처리:

```java
@RestController
public class PublicController {

    @GetMapping("/health")
    @PublicEndpoint  // 인증 불필요
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
```

#### 역할 기반 인가

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/users")
    @RequireRole("ADMIN")  // ADMIN 역할 필요
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/users/{userId}/ban")
    @RequireRole({"ADMIN", "MODERATOR"})  // 여러 역할 허용
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        adminService.banUser(userId);
        return ResponseEntity.ok().build();
    }
}
```

#### 사용자 정보 접근

```java
@Service
public class OrderService {

    public Order createOrder(CreateOrderRequest request) {
        SecurityUserInfo userInfo = SecurityHolder.getCurrentUser();

        Order order = Order.builder()
            .userId(userInfo.getUserId())
            .username(userInfo.getUsername())
            .userEmail(userInfo.getEmail())
            .items(request.getItems())
            .build();

        return orderRepository.save(order);
    }
}
```

#### AWS API Gateway 통합

API Gateway 환경에서는 JWT 검증이 Gateway에서 수행되어 성능상 이점 제공:

1. **성능 향상**: 각 마이크로서비스에서 JWT 검증 과정 생략
2. **Redis 부하 감소**: 토큰 블랙리스트 검증을 Gateway에서 일괄 처리
3. **보안 강화**: Gateway에서 중앙집중식 토큰 검증

## 🔧 배포 방법

### Jenkins 파이프라인

모듈별 선택적 배포를 지원하는 Jenkins 파이프라인 사용

```bash
# 코어 모듈만 배포
./gradlew publishCore

# 코어 + 카프카 모듈 배포
./gradlew publishWithKafka

# 코어 + 레디스 모듈 배포
./gradlew publishWithRedis

# 코어 + 파일(S3) 모듈 배포
./gradlew publishWithFile

# 코어 + JPA 모듈 배포
./gradlew publishWithJpa

# 코어 + RDB 모듈 배포
./gradlew publishWithRdb

# 코어 + 시큐리티 클라이언트 모듈 배포
./gradlew publishWithSecurityClient

# 코어 + 시큐리티 인증서버 모듈 배포
./gradlew publishWithSecurityAuth

# 코어 + 모든 시큐리티 모듈 배포
./gradlew publishWithSecurity

# 모든 모듈 배포
./gradlew publishAll
```

### 배포 옵션

- **CORE_ONLY**: 코어 모듈만 배포
- **WITH_KAFKA**: 코어 + 카프카 모듈 배포
- **WITH_REDIS**: 코어 + 레디스 모듈 배포
- **WITH_FILE**: 코어 + 파일(S3) 모듈 배포
- **WITH_JPA**: 코어 + JPA 모듈 배포
- **WITH_RDB**: 코어 + RDB 모듈 배포
- **WITH_SECURITY_CLIENT**: 코어 + 레디스 + 시큐리티 클라이언트 모듈 배포
- **WITH_SECURITY_AUTH**: 코어 + 레디스 + 카프카 + 시큐리티 인증서버 모듈 배포
- **WITH_SECURITY**: 코어 + 레디스 + 카프카 + 모든 시큐리티 모듈 배포
- **ALL_MODULES**: 모든 모듈 배포

### 로컬 개발 환경

```bash
# 로컬 개발용 빌드 (코어 모듈 참조 포함)
./gradlew build -PlocalDev=true

# 로컬 개발용 테스트
./gradlew test -PlocalDev=true
```

## 🛠️ 설정

설정 예시는 `application.yml`을 참조


```

## 📚 트러블슈팅

### OAuth2 로그인 실패
1. 클라이언트 ID/Secret 확인
2. 리다이렉트 URI 설정 확인
3. OAuth2 제공자 콘솔에서 애플리케이션 상태 확인

### JWT 토큰 검증 실패
1. 시크릿 키 일치 여부 확인
2. 토큰 만료시간 확인
3. Redis 연결 상태 확인

### Redis 연결 오류
1. Redis 서버 상태 확인
2. 연결 정보 (호스트, 포트, 패스워드) 확인
3. 네트워크 연결 상태 확인

### Jenkins 배포 이슈
1. Gradle 권한 오류: `chmod +x ./gradlew` 확인
2. 넥서스 연결 실패: VPN 연결 상태 및 넥서스 서버 접근 가능 여부 확인
3. 빌드 실패: 선택한 브랜치의 코드 상태 및 의존성 문제 확인

## ⚠️ 주의사항

1. **코어 모듈은 필수**: 다른 모듈들을 사용할 때는 항상 `kyj-lib-core`도 함께 포함해야 합니다.

2. **로컬 개발시**: 서브 모듈에서 코어 기능을 참조해야 할 때는 `-PlocalDev=true` 플래그를 사용합니다.



