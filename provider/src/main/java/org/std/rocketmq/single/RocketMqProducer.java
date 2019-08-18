package org.std.rocketmq.single;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.std.rocketmq.RocketMqMessage;

import com.alibaba.fastjson.JSON;

@Component
public class RocketMqProducer {

	@Value("${std.rocketmq.producerGroup}")
	private String producerGroup;
	@Value("${std.rocketmq.namesrvaddr}")
	private String namesrvaddr;
	
	private DefaultMQProducer producer = null;
	
	@PostConstruct
	public void producer() {
		producer = new DefaultMQProducer(producerGroup);
		producer.setNamesrvAddr(namesrvaddr);
		try {
			producer.start();
			for (int i = 0; i < 10; i++) {
				RocketMqMessage rocketMqMessage = new RocketMqMessage(i+"", "消息"+i, "RocketMQ发送消息"+i);
				String messageJson = JSON.toJSONString(rocketMqMessage);
				Message message = new Message("message-topic", "message-tag", messageJson.getBytes(RemotingHelper.DEFAULT_CHARSET));
				SendResult result = producer.send(message);
				System.out.println("发送响应：MsgId：" + result.getMsgId() + "，发送状态：" + result.getSendStatus());
			}
		} catch (MQClientException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (RemotingException e) {
			e.printStackTrace();
		} catch (MQBrokerException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void shutDownProducer() {
		producer.shutdown();
	}
}
