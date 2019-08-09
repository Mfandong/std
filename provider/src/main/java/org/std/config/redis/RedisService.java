package org.std.config.redis;

public interface RedisService {
	/**
	 * 通过key删除
	 * @param keys
	 */
	void del(String... keys);
	/**
	 * 添加缓存  单位秒
	 * @param key
	 * @param value
	 * @param timeout
	 */
	void set(String key, String value, long timeout);
	/**
	 * 添加缓存
	 * @param key
	 * @param value
	 */
	void set(String key, String value);
	/**
	 * 添加NXkey value
	 * 没有会添加，返回true,已有不会添加，返回false
	 * @param key
	 * @param value
	 * @param timeout
	 */
	boolean setNX(String key, String value);
	
	/**
	 * 设置有效期的NX值
	 * 单位：秒
	 * @param key
	 * @param value
	 * @param timeout
	 * @return
	 */
	boolean setNX(String key, String value, long timeout);
	/**
	 * 获取redis值
	 * @param key
	 * @return
	 */
	String get(String key);
	/**
	 * 判断key值是否存在
	 * @param key
	 * @return
	 */
	boolean exists(String key);
}
