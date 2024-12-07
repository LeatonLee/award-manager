package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 设置序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // 设置Key序列化器
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class)); // 设置Value序列化器

        return redisTemplate;
    }

    // RedisConnectionFactory配置
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 配置Redis连接工厂
        return new LettuceConnectionFactory("localhost", 6379); // 连接Redis的服务器地址和端口
    }
}
