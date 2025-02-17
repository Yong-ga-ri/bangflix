package com.swcamp9th.bangflixbackend.shared.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

public class MDCFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            // HTTP 요청일 경우, Correlation ID를 생성 또는 헤더에서 가져오기
            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                String correlationId = httpRequest.getHeader("X-Correlation-Id");
                if (correlationId == null || correlationId.isEmpty()) {
                    correlationId = UUID.randomUUID().toString();
                }
                MDC.put("correlationId", correlationId);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
