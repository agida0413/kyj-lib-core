                //실제 프로젝트설정
                testImplementation 'org.springframework.boot:spring-boot-starter-test'
                testImplementation 'org.springframework.security:spring-security-test'
                testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
        
        
                annotationProcessor 'org.projectlombok:lombok'



				//실제프로젝트 설정
        //		annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
        //		annotationProcessor "jakarta.annotation:jakarta.annotation-api"
        //		annotationProcessor "jakarta.persistence:jakarta.persistence-api"
 
시큐리티 모듈 2종 중 
1. 클라이언트만 사용하는 경우:

// 애플리케이션의 build.gradle
implementation project(':kyj-lib-core-security-client')
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-web'

// JWT 런타임 구현체도 필요
runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'

2. 인증 서버만 사용하는 경우:

implementation project(':kyj-lib-core-security-auth')
// security-auth가 이미 api로 필요한 의존성들을 가져옴

3. 둘 다 사용하는 경우:

implementation project(':kyj-lib-core-security-client')
implementation project(':kyj-lib-core-security-auth')
// security-auth가 필요한 의존성들을 제공
