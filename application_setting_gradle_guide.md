                //실제 프로젝트설정
                testImplementation 'org.springframework.boot:spring-boot-starter-test'
                testImplementation 'org.springframework.security:spring-security-test'
                testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
        
        
                annotationProcessor 'org.projectlombok:lombok'



				//실제프로젝트 설정
        //		annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
        //		annotationProcessor "jakarta.annotation:jakarta.annotation-api"
        //		annotationProcessor "jakarta.persistence:jakarta.persistence-api"
