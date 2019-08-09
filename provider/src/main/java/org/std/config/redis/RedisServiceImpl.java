package org.std.config.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService{
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Override
	public void del(String... keys) {
		for (String key : keys) {
			redisTemplate.delete(key);
		}
	}

	@Override
	public void set(String key, String value, long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}

	@Override
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public boolean setNX(String key, String value) {
		return redisTemplate.opsForValue().setIfAbsent(key, value);
	}

	@Override
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.hasKey(key);
	}

	@Override
	public boolean setNX(String key, String value, long timeout) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
	}

}
