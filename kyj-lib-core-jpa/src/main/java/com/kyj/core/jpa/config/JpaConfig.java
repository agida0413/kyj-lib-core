package com.kyj.core.jpa.config;

import com.kyj.core.security.client.util.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA기본 CONFIG
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    /**
     * BaseEntity 등록자,수정자
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider(){
        return () -> Optional.ofNullable(SecurityContext.getUsername());
    }
}
