package com.swcamp9th.bangflixbackend.shared.aop;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Order;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(2)
public class GlobalPerformanceLoggingAspect {

    @Around("execution(* com.swcamp9th.bangflixbackend..*(..)) && !target(javax.servlet.Filter)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("logExecutionTime");
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            long duration = end - start;

            // 메서드 정보와 실행 시간을 구조화된 형태로 로그 기록
            log.info("Method: {} executed in {} ms",
                    joinPoint.getSignature().toShortString(), duration);
        }
    }
}
