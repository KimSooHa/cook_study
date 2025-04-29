package com.study.cook.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.Cache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CacheController {

    private final CacheManager cacheManager;


    @GetMapping("/cache/all")
    public Map<String, List<String>> getAllCaches() {
        log.info("📦 캐시 전체 조회 시작!");
        Map<String, List<String>> result = new HashMap<>();

        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache == null) continue;

            // 캐시의 nativeCache 가져오기 (JCache Cache 로 다운캐스팅)
            javax.cache.Cache<Object, Object> nativeCache =
                    (javax.cache.Cache<Object, Object>) cache.getNativeCache();

            List<String> valueList = new ArrayList<>();
            for (Cache.Entry<Object, Object> entry : nativeCache) {
                Object value = entry.getValue();
                valueList.add(value != null ? value.toString() : "null");
            }

            result.put(cacheName, valueList);
        }

        log.info("✅ 캐시 전체 조회 완료!");
        return result;
    }
}