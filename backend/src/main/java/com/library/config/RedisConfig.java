package com.library.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 缓存配置
 * <p>
 * 提供：
 * 1. RedisTemplate 统一序列化（key=String, value=Jackson JSON）
 * 2. CacheManager 按缓存名设置不同 TTL（dashboard 30s、详情 120s、列表 60s）
 * 3. CacheErrorHandler 降级处理 —— Redis 不可用时只打日志，请求穿透到 DB
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisConfig implements CachingConfigurer {

    /**
     * 按缓存名定义 TTL（秒）
     */
    private static final Map<String, Duration> TTL_MAP = new HashMap<>();

    static {
        TTL_MAP.put("dashboard", Duration.ofSeconds(30));   // 仪表盘聚合数据，变化频繁
        TTL_MAP.put("books", Duration.ofSeconds(60));         // 图书列表
        TTL_MAP.put("book", Duration.ofSeconds(120));         // 图书详情
        TTL_MAP.put("readers", Duration.ofSeconds(60));       // 读者列表
        TTL_MAP.put("reader", Duration.ofSeconds(120));       // 读者详情
        TTL_MAP.put("borrowings", Duration.ofSeconds(60));    // 借阅列表
        TTL_MAP.put("notifications", Duration.ofSeconds(30)); // 通知列表
    }

    // ========== RedisTemplate ==========

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // key 用 String 序列化（可读性好）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // value 用 Jackson JSON 序列化（比 JDK 序列化体积小、可读性好、无需实现 Serializable）
        GenericJackson2JsonRedisSerializer jsonSerializer = jacksonSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // ========== CacheManager ==========

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = defaultCacheConfig();

        // 为每个缓存名生成单独的配置（不同 TTL）
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        for (Map.Entry<String, Duration> entry : TTL_MAP.entrySet()) {
            cacheConfigs.put(entry.getKey(), defaultConfig.entryTtl(entry.getValue()));
        }

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

    // ========== 降级 CacheErrorHandler ==========
    // Redis 挂掉时不抛异常，只打 warn 日志，让请求继续走 DB

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("[Redis降级] GET失败 cache={} key={}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("[Redis降级] PUT失败 cache={} key={}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("[Redis降级] EVICT失败 cache={} key={}: {}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("[Redis降级] CLEAR失败 cache={}: {}", cache.getName(), e.getMessage());
            }
        };
    }

    // ========== 内部工具 ==========

    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))               // 默认 TTL 60s
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair.fromSerializer(jacksonSerializer()))
                .disableCachingNullValues()                      // 不缓存 null，防止穿透
                .computePrefixWith(cacheName -> "library:" + cacheName + ":");
    }

    private GenericJackson2JsonRedisSerializer jacksonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
