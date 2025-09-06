# KYJ Core

> 사이드 프로젝트 개발 속도 향상을 위한 Spring Boot 기반 공통 모듈 라이브러리

## 📖 개요

KYJ Core는 Spring Boot 기반의 웹 애플리케이션 개발 시 반복적으로 사용되는 기능들을 모듈화하여, 사이드 프로젝트의 개발 속도를 대폭 향상시키기 위해 만들어진 공통 라이브러리입니다.

## 🚀 주요 기능

### 🔧 유틸리티 모듈
- **RandomGenerator**: 보안 강화된 랜덤 문자열/숫자 생성기
- **CookieUtil**: 쿠키 관리 유틸리티
- **GeoUtil**: 지리정보 처리 유틸리티
- **MailUtil**: 이메일 전송 유틸리티

### 🗄️ 데이터 저장 및 캐싱
- **Redis 설정**: 단일/클러스터 Redis 연동 및 캐시 관리
- **MyBatis 연동**: 데이터베이스 ORM 설정
- **MySQL 연결**: 데이터베이스 연결 및 로깅

### 📧 메일 서비스
- **JavaMailSender**: 이메일 전송 기능
- **템플릿 기반 메일**: HTML 템플릿을 활용한 메일 발송

### 📁 파일 관리
- **FileService**: 파일 업로드/다운로드 처리
- **S3 연동**: AWS S3 객체 스토리지 연동
- **파일 검증**: 업로드 파일 유효성 검사

### 🔐 보안 및 인증
- **Spring Security**: 기본 보안 설정
- **OAuth2**: 소셜 로그인 연동
- **JWT**: JSON Web Token 처리

### 📊 모니터링
- **Spring Actuator**: 애플리케이션 헬스체크
- **Prometheus**: 메트릭 수집 및 모니터링

### 🔄 비동기 처리
- **Kafka**: 메시지 큐 연동
- **비동기 서비스**: 백그라운드 작업 처리

### 🎯 기타 기능
- **AOP**: 횡단 관심사 처리
- **예외 처리**: 커스텀 예외 및 글로벌 예외 핸들러
- **트랜잭션**: 데이터베이스 트랜잭션 관리
- **로깅**: 구조화된 로깅 시스템

## 🛠 기술 스택

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security**
- **MyBatis**
- **Redis (Lettuce)**
- **AWS S3 SDK**
- **Kafka**
- **JWT (jjwt)**
- **Lombok**
- **JTS (지리정보)**

## 📦 설치 및 사용

### Gradle 의존성 추가

```gradle
dependencies {
    implementation 'com.kyj.fmk:kyj-fk-be-core:0.0.1-SNAPSHOT'
}
```

### Maven Repository 설정

프로젝트의 `build.gradle`에 다음 저장소를 추가

```gradle
repositories {
    maven {
        url = uri("http://192.168.56.1:9090/repository/bottle-story/")
    }
}
```

## 🏗 프로젝트 구조

```
src/main/java/com/kyj/fmk/core/
├── async/          # 비동기 처리
├── exception/      # 예외 처리
├── file/          # 파일 관리
├── logging/       # 로깅
├── mail/          # 메일 서비스
├── model/         # 데이터 모델
├── redis/         # Redis 설정
├── service/       # 공통 서비스
├── tx/           # 트랜잭션 처리
└── util/         # 유틸리티 클래스
```

## 🎯 사용 예시

### 랜덤 문자열 생성
```java
// 16자리 보안 강화 랜덤 문자열 생성
String randomPassword = RandomGenerator.generateRandom(16);

// 6자리 랜덤 숫자 생성
String randomCode = RandomGenerator.generateRandomNumber(6);
```

### Redis 사용
```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 데이터 저장
redisTemplate.opsForValue().set("key", "value");

// 데이터 조회
String value = redisTemplate.opsForValue().get("key");
```

### 메일 전송
```java
@Autowired
private MailSender mailSender;

// 템플릿 메일 전송
mailSender.send("welcome-template", "환영합니다!", "user@example.com");
```

## ⚙️ 설정 요구사항

애플리케이션에서 다음과 같은 설정이 필요합니다:

```yaml
spring:
  redis:
    host: localhost
    port: 6379
  
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    
  datasource:
    url: jdbc:mysql://localhost:3306/your-database
    username: your-username
    password: your-password
```



이 프로젝트는 개인 사이드 프로젝트용으로 개발되었습니다.

---

