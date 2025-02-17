package com.swcamp9th.bangflixbackend.domain.healthcheck;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
public class HealthCheck {

    private final Environment environment;

    @Autowired
    public HealthCheck(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/server-port")
    public String getServerPort() {
        // 'server.port' 값을 통해 실행 중인 포트 확인
        String port = environment.getProperty("local.server.port");
        log.info("server port: {}!", port);
        return "Spring Boot server is running on port: " + port;
    }
}

