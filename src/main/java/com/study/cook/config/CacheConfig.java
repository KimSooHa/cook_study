package com.study.cook.config;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
@EnableCaching // Spring의 cache 기능 활성화
public class CacheConfig {

    @Bean
    public javax.cache.CacheManager ehCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = provider.getCacheManager();

        // 1. recipeCache: 10분 TTL
        cacheManager.createCache("recipeCache", Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Object.class, Object.class, ResourcePoolsBuilder.heap(100)
                        ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(20)))
                        .build()
        ));

        // 2. popularRecipeListCache
        cacheManager.createCache("popularRecipeListCache", Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Object.class, Object.class, ResourcePoolsBuilder.heap(100)
                        ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(15)))
                        .build()
        ));

        // 3. popularClubListCache
        cacheManager.createCache("popularClubListCache", Eh107Configuration.fromEhcacheCacheConfiguration(
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                Object.class, Object.class, ResourcePoolsBuilder.heap(100)
                        ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)))
                        .build()
        ));

        return cacheManager;
    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        return new JCacheCacheManager(ehCacheManager());
    }
}