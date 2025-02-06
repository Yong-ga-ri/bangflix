package com.swcamp9th.bangflixbackend.shared.filter;

import com.swcamp9th.bangflixbackend.shared.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class RequestFilter implements Filter {
    private final JwtUtil jwtUtil;
    public RequestFilter(JwtUtil jwtutil) {
        this.jwtUtil = jwtutil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) servletRequest);
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        log.debug("authorizationHeader: {}", authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.replace("Bearer ", "");
            String loginId = jwtUtil.getSubjectFromToken(token);
            if (loginId != null) {
                log.debug("loginId: {}", loginId);
                httpServletRequest.setAttribute("loginId", loginId);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }
}
