package com.swcamp9th.bangflixbackend.shared.filter;

import com.swcamp9th.bangflixbackend.shared.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {
    private final JwtUtil jwtUtil;

    @Autowired
    public FilterConfiguration(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public FilterRegistrationBean<RequestFilter> logFilter() {
        FilterRegistrationBean<RequestFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RequestFilter(jwtUtil));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/api/*");
        return filterRegistrationBean;
    }
}
