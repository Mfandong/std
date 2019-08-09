package org.std.service;

import java.util.List;

import org.std.model.OrderInfo;

public interface IOrderService {

	List<OrderInfo> getOrderInfos();
	
	OrderInfo getOrderInfoById(String orderid);
}
