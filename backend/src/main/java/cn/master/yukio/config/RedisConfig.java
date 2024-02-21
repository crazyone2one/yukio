package cn.master.yukio.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * @author Created by 11's papa on 02/21/2024
 **/
@Configuration
public class RedisConfig {
    @Bean
    public <T> RedisTemplate<String, T> redisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .indentOutput(false)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .modules(
                        // Optional
                        new Jdk8Module(),
                        // Dates/Times
                        new JavaTimeModule()
                )
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS,
                        SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
                )
                .build();
        /* 使用 GenericJackson2JsonRedisSerializer 替换默认序列化 */
        GenericJackson2JsonRedisSerializer jacksonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        /* 设置 key 和 value 的序列化规则（key is String，value is JSON） */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jacksonSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jacksonSerializer);

        /* 序列化完成 */
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
