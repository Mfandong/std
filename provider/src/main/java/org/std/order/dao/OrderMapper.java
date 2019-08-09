package org.std.order.dao;

import org.std.model.OrderInfo;

public interface OrderMapper {

	OrderInfo getOrderInfoById(String orderid);
}
