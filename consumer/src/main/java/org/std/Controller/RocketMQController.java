package org.std.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.std.service.IRocketMQService;

import com.alibaba.dubbo.config.annotation.Reference;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {
	@Reference(version="1.0.0")
	private IRocketMQService rocketMQService;
	
	@GetMapping("/sendMsg")
	public String sendMsg(){
		rocketMQService.sendMsg();
		return "rocketMQ发送消息成功！";
	}
	
	@GetMapping("/sendTransactionMsg")
	public String sendTransactionMsg(){
		return rocketMQService.sendTransactionMsg();
	}
	
	@GetMapping("/sendMsgOrder")
	public String sendMsgOrder(){
		rocketMQService.sendMsgOrder();
		return "rocketMQ发送按顺序的消息成功";
	}
}
