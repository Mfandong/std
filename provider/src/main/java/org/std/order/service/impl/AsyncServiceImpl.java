package org.std.order.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.std.model.OrderInfo;
import org.std.order.dao.OrderMapper;
import org.std.order.service.IAsyncService;

@Service
public class AsyncServiceImpl implements IAsyncService{
	@Autowired
	private OrderMapper orderMapper;
	
	@Async("asyncServiceExecutor")
	@Override
	public OrderInfo asyncGetOrderInfoById(String orderid) {
		System.out.println("线程名称：" + Thread.currentThread().getName());
		return orderMapper.getOrderInfoById(orderid);
	}

}
