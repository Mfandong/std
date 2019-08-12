package org.std.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.std.model.OrderInfo;
import org.std.order.service.IOrderbaseService;
import org.std.service.IOrderService;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version="1.0.0", interfaceClass=IOrderService.class)
@Component
public class OrderServiceImpl implements IOrderService{
	@Autowired
	private IOrderbaseService orderBaseService;
	
	public List<OrderInfo> getOrderInfos() {
		return null;
	}

	public OrderInfo getOrderInfoById(String orderid) {
		return orderBaseService.getOrderInfoByOrderid(orderid);
	}

}
