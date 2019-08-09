package org.std.order.service;

import org.std.model.OrderInfo;

public interface IOrderbaseService {

	OrderInfo getOrderInfoByOrderid(String orderid);
	
	OrderInfo getOrderInfoByIdFromMain(String orderid);
	
	OrderInfo getOrderInfoByIdFromSlave(String orderid);
	
	OrderInfo getOrderInfoByIdFromHeader(String orderid);
}
