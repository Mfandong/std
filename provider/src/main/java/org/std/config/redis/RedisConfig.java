package org.std.config.redis;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching  //开启注解
public class RedisConfig {

	@Bean
	public KeyGenerator customKeyGenerator(){
		return (target, method, params) -> {
			StringBuilder sb = new StringBuilder();
//			String targetClass = target.getClass().getName().substring(target.getClass().getName().lastIndexOf(".")+1);
//			sb.append(targetClass).append(":");
//			sb.append(method.getName()).append(":");
			if (params != null){
				for(Object obj : params){
					sb.append(obj.toString()).append(":");
				}
			}
			return sb.toString();
		};
//		return new KeyGenerator() {
//			
//			@Override
//			public Object generate(Object target, Method method, Object... params) {
//				return null;
//			}
//		};
	}
	
	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory){
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		//实现序列化和反序列化redis key值
		template.setKeySerializer(new StringRedisSerializer());
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		
		template.setValueSerializer(jackson2JsonRedisSerializer);
		return template;
	}
	
	@Bean
	public CacheManager customCacheManager(@SuppressWarnings("rawtypes") RedisTemplate redisTemplate){
		//设置缓存过期时间
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1));
		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
		return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
	}
}
