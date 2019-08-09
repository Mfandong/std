package org.std.order.service;

import org.std.model.OrderInfo;

public interface IRedisCacheService {
	
	OrderInfo getCacheById(String id);
	
	OrderInfo getCacheByName(String name);
	
	void updateOrderInfo(OrderInfo orderInfo);
	
	void clearValueAllCache();
	
	void clearManyValueCache();
}
