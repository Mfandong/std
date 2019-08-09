package org.std.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.std.config.datasource.TargetDataSource;
import org.std.model.OrderInfo;
import org.std.order.dao.OrderMapper;
import org.std.order.service.IOrderbaseService;

@Service
public class OrderServiceImpl implements IOrderbaseService{
	@Autowired
	private OrderMapper orderMapper;
	public OrderInfo getOrderInfoByOrderid(String orderid) {
		return orderMapper.getOrderInfoById(orderid);
	}
	
	@TargetDataSource("main")
	public OrderInfo getOrderInfoByIdFromMain(String orderid) {
		return orderMapper.getOrderInfoById(orderid);
	}
	
	@TargetDataSource("slave")
	public OrderInfo getOrderInfoByIdFromSlave(String orderid) {
		return orderMapper.getOrderInfoById(orderid);
	}
	
	@TargetDataSource("leader")
	public OrderInfo getOrderInfoByIdFromHeader(String orderid) {
		return orderMapper.getOrderInfoById(orderid);
	}

}
