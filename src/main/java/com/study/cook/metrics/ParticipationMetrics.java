package com.study.cook.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ParticipationMetrics {

    private final Counter successCounter;
    private final Counter failCounter;

    public ParticipationMetrics(MeterRegistry registry) {
        this.successCounter = registry.counter("club.participation.success");
        this.failCounter = registry.counter("club.participation.fail");
    }

    public void incrementSuccess() {
        successCounter.increment();
    }

    public void incrementFail() {
        failCounter.increment();
    }
}