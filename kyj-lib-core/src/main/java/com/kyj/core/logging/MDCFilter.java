package com.kyj.core.logging;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 2025-10-03
 * @author 김용준
 * 서블릿 필터에 로그 트레이스 추적을 위한 MDC필터를 등록한다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final UUID uuid = UUID.randomUUID();
        MDC.put("context_id",uuid.toString());
        filterChain.doFilter(servletRequest,servletResponse);
        MDC.clear();
    }
}
