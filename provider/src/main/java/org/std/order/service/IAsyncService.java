package org.std.order.service;

import org.std.model.OrderInfo;

public interface IAsyncService {
	
	OrderInfo asyncGetOrderInfoById(String orderid);
}
