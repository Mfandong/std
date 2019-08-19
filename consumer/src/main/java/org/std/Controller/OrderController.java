package org.std.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.std.model.OrderInfo;
import org.std.service.IOrderService;
import org.std.vo.Result;
import org.std.vo.ResultUtils;

import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Reference(version="1.0.0", retries=3)
	private IOrderService orderService;
	
	@RequestMapping("/getOrderById")
	public Result getOrderById(@RequestParam String orderid){
		try {
			OrderInfo orderInfo = orderService.getOrderInfoById(orderid);
			return ResultUtils.success(orderInfo);
		} catch (Exception e) {
			e.printStackTrace();
			return ResultUtils.err("获取订单信息异常");
		}
	}
}
