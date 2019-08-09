package org.std.order.service.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class CacheClearService {
	
	/** 根据key删除指定的缓存数据 */
	@CacheEvict(cacheManager="customCacheManager", keyGenerator="customKeyGenerator", beforeInvocation=true, value="test:order")
	public void clearOrderInfoById(String id){
		
	}
	/** 删除指定value下的所有缓存数据 */
	@CacheEvict(value="test:order", allEntries=true, beforeInvocation=true)
	public void clearAllOrderInfo(){
		
	}
	
	/** 删除多个value下的缓存数据 */
	@Caching(evict={
			@CacheEvict(value="test:order", allEntries=true, beforeInvocation=true),
			@CacheEvict(value="test:neworder", allEntries=true, beforeInvocation=true)
	})
	public void clearManyOrderInfo(){
		
	}
}
