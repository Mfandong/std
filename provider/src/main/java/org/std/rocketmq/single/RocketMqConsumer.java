package org.std.rocketmq.single;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RocketMqConsumer {
	@Value("${std.rocketmq.consumerGroup}")
	private String consumerGroup;
	@Value("${std.rocketmq.namesrvaddr}")
	private String namesrvaddr;
	
	private DefaultMQPushConsumer consumer = null;
	
	@PostConstruct
	public void consumer() {
		System.out.println("init defaultMQPushConsumer");
		consumer = new DefaultMQPushConsumer(consumerGroup);
		consumer.setNamesrvAddr(namesrvaddr);
		try {
			consumer.subscribe("message-topic", "message-tag");
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
					for (MessageExt messageExt : msgs) {
						System.out.println("消费消息：" + new String(messageExt.getBody()));
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void consumerShutdown() {
		consumer.shutdown();
	}
}
