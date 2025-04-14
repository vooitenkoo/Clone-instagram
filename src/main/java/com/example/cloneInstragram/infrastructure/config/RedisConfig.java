package com.example.cloneInstragram.infrastructure.config;

import com.example.cloneInstragram.application.chat.dto.ChatDTO;
import com.example.cloneInstragram.application.chat.dto.MessageDTO;
import com.example.cloneInstragram.domain.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public ObjectMapper objectMapper2() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory, ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<User> userSerializer = new Jackson2JsonRedisSerializer<>(objectMapper, User.class);

        Jackson2JsonRedisSerializer<List<ChatDTO>> chatListSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ChatDTO.class)
        );

        Jackson2JsonRedisSerializer<List<MessageDTO>> messageListSerializer = new Jackson2JsonRedisSerializer<>(
                objectMapper,
                objectMapper.getTypeFactory().constructCollectionType(List.class, MessageDTO.class)
        );

        // Конфигурация для кэшей с User
        RedisCacheConfiguration userCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(userSerializer));

        // Конфигурация для кэша userChats (List<ChatDTO>)
        RedisCacheConfiguration chatCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(chatListSerializer));

        // Конфигурация для кэша chatMessagesList (List<MessageDTO>)
        RedisCacheConfiguration messageCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(messageListSerializer));

        // Настройка кэшей
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("users", userCacheConfig);
        cacheConfigurations.put("usersById", userCacheConfig);
        cacheConfigurations.put("userChats", chatCacheConfig);
        cacheConfigurations.put("chatMessagesList", messageCacheConfig);

        return RedisCacheManager.builder(factory)
                .cacheDefaults(userCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }
}