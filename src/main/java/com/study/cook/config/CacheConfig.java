package com.study.cook.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching // Spring의 cache 기능 활성화
public class CacheConfig {

//    @Bean
    public javax.cache.configuration.Configuration<Object, Object> recipeCacheConfiguration() {
        return Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(
                                Object.class, // cache key 타입
                                Object.class, // cache value 타입
                                ResourcePoolsBuilder.heap(100) // 힙 메모리에 최대 100까지 객체 저장
                        )
                        // 캐시 만료 정책
//                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10))) // 캐시 항목 생성 후 n분 지나면 자동 삭제
                        .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofMinutes(10))) // 접근 여부와 관계없이 무조건 10분 유지 후 제거
                        .build()
        );
    }

    // cache 객체
    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();
        cacheManager.createCache("recipeCache", recipeCacheConfiguration());
        return new JCacheCacheManager(cacheManager); // Spring CacheManager로 wrapping 해주기
    }
}