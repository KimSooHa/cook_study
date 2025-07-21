package com.study.cook.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect // aop 구현을 위한 proxy 생성 등을 자동으로 해줌
@Component
@RequiredArgsConstructor
public class RepositoryTimingAspect {

    private final MeterRegistry registry;

    /*
        첫 번째 *: 리턴 타입 무관
        com.study.cook.repository..: 이 패키지와 그 하위 모든 패키지
        *.*(..): 모든 클래스의 모든 메서드, 모든 파라미터
    */
    // @Around : 지정된 패턴에 해당하는 메소드의 실행되기 전, 실행된 후 모두에서 동작
    // Repository 하위 패키지의 모든 메서드를 타겟으로 함
    @Around("execution(* com.study.cook.repository..*.*(..))") // execution(...): 메서드 실행을 대상으로 함
    public Object measureTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("📊 [Repository Timing] {}.{} took {} ms", className, methodName, duration);
            // Prometheus에 메트릭 기록
            // Timer.builder(...).tag(...)
            // : 메서드 이름과 클래스명을 Prometheus의 tag로 추가하여 메서드별 분석 가능.
            Timer.builder("repository.method.timing")
                    .description("Repository 메서드 실행 시간")
                    .tags("class", className, "method", methodName)
                    .register(registry)
                    .record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
}