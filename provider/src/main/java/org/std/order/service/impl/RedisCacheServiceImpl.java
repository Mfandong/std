package org.std.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.std.model.OrderInfo;
import org.std.order.dao.OrderMapper;
import org.std.order.service.IRedisCacheService;

@Service
public class RedisCacheServiceImpl implements IRedisCacheService{
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private CacheClearService cacheClearService;
	@Override
	@Cacheable(cacheManager="customCacheManager", keyGenerator="customKeyGenerator", value="test:order")
	public OrderInfo getCacheById(String id) {
		return orderMapper.getOrderInfoById(id);
	}

	@Override
	@Cacheable(cacheManager="customCacheManager", keyGenerator="customKeyGenerator", value="test:neworder")
	public OrderInfo getCacheByName(String name) {
		return orderMapper.getOrderInfoById(name);
	}

	@Override
	public void updateOrderInfo(OrderInfo orderInfo) {
		cacheClearService.clearOrderInfoById(orderInfo.getOrderid());
	}

	@Override
	public void clearValueAllCache() {
		cacheClearService.clearAllOrderInfo();
	}

	@Override
	public void clearManyValueCache() {
		cacheClearService.clearManyOrderInfo();
	}
}
