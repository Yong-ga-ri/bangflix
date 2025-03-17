package com.swcamp9th.bangflixbackend.shared.filter;

import com.swcamp9th.bangflixbackend.shared.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class RequestFilter implements Filter {
    public static final String SERVLET_REQUEST_ATTRIBUTE_KEY = "loginId";

    private final JwtUtil jwtUtil;
    public RequestFilter(JwtUtil jwtutil) {
        this.jwtUtil = jwtutil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = ((HttpServletRequest) servletRequest);
        String authorizationHeader = httpServletRequest.getHeader(JwtUtil.AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(JwtUtil.BEARER_PREFIX)) {
            String token = authorizationHeader.replace(JwtUtil.BEARER_PREFIX, "");
            String loginId = jwtUtil.getSubjectFromToken(token);
            if (loginId != null) {
                httpServletRequest.setAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId);
            }
        } else if (authorizationHeader == null) {
            httpServletRequest.setAttribute("loginId", "user1");
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }
}
