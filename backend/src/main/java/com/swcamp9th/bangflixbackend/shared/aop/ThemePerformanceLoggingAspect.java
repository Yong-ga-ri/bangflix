package com.swcamp9th.bangflixbackend.shared.aop;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(1)
public class ThemePerformanceLoggingAspect {

    private final MeterRegistry meterRegistry;

    @Autowired
    public ThemePerformanceLoggingAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("execution(* com.swcamp9th.bangflixbackend.domain.theme.service..*(..))")
    public Object logExecutionTimeOnThemeServiceLayer(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = Timer.start(meterRegistry);
        long startTime = System.currentTimeMillis();  // 시작 시간 기록
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - startTime;  // 경과 시간 계산
            sample.stop(meterRegistry.timer("method.execution.time",
                    "method", joinPoint.getSignature().toShortString()));
            log.info("Method {} executed in {} ms",
                    joinPoint.getSignature().toShortString(), duration);
        }
    }
}
