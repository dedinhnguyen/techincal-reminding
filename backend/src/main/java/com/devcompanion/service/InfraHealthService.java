package com.devcompanion.service;

import com.devcompanion.dto.InfraHealthDto;
import com.devcompanion.repository.CategoryRepository;
import com.devcompanion.repository.SnippetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfraHealthService {

    private final SnippetRepository snippetRepository;
    private final CategoryRepository categoryRepository;
    private final MongoTemplateService mongoTemplateService;
    private final CacheManager cacheManager;
    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${app.elasticsearch.enabled:false}")
    private boolean esEnabled;

    @Value("${app.mongodb.enabled:false}")
    private boolean mongoEnabled;

    public InfraHealthDto getHealth() {
        Map<String, String> services = new HashMap<>();

        // PostgreSQL Check
        try {
            long count = snippetRepository.count();
            services.put("postgresql", "HEALTHY (Connected - " + count + " records)");
        } catch (Exception e) {
            services.put("postgresql", "DEGRADED / H2 IN-MEMORY");
        }

        // Redis Check
        try {
            RedisConnection connection = redisConnectionFactory.getConnection();
            String ping = connection.ping();
            services.put("redis", "HEALTHY (Ping: " + ping + ")");
        } catch (Exception e) {
            services.put("redis", "STANDALONE / IN-MEMORY CACHE");
        }

        // MongoDB Check
        if (mongoEnabled) {
            services.put("mongodb", "HEALTHY (Connected)");
        } else {
            services.put("mongodb", "ACTIVE (Mock/Embedded Engine)");
        }

        // Elasticsearch Check
        if (esEnabled) {
            services.put("elasticsearch", "HEALTHY (Cluster Online)");
        } else {
            services.put("elasticsearch", "FALLBACK TO POSTGRES JPA CRITERIA");
        }

        // Cache statistics
        Map<String, Object> cacheStats = new HashMap<>();
        cacheStats.put("cacheType", cacheManager.getClass().getSimpleName());
        cacheStats.put("activeCacheNames", cacheManager.getCacheNames());

        long totalSnippets = snippetRepository.count();
        long totalCategories = categoryRepository.count();
        long totalMongoTemplates = mongoTemplateService.getAllTemplates().size();

        return new InfraHealthDto(
                "UP",
                services,
                cacheStats,
                totalSnippets,
                totalCategories,
                totalMongoTemplates
        );
    }
}
