package org.std.config.rocketmq;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQConfiguration {
	@Autowired
	private RocketMQProperties rocketMQProperties;
	
	//事件监听
	@Autowired
	private ApplicationEventPublisher publisher = null;
	
	private static boolean isFirstSub = true;
	
	private static long startTime = System.currentTimeMillis();
	
	private static Logger logger = LoggerFactory.getLogger(RocketMQConfiguration.class);
	@Bean
	public DefaultMQProducer defaultProducer() throws MQClientException {
		DefaultMQProducer producer = new DefaultMQProducer(rocketMQProperties.getProducerGroupName());
		producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
		producer.setInstanceName(rocketMQProperties.getProducerInstanceName());
		producer.setVipChannelEnabled(false);
		producer.setRetryTimesWhenSendAsyncFailed(10);
		producer.start();
		logger.info("rocketmq producer server is starting...");
		return producer;
	}
	
	@Bean
	public TransactionMQProducer transactionProducer() throws MQClientException {
		TransactionMQProducer producer = new TransactionMQProducer(rocketMQProperties.getTransactionProducerGroupName());
		producer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
		producer.setInstanceName(rocketMQProperties.getProducerTranInstanceName());;
		producer.setRetryTimesWhenSendAsyncFailed(10);
		//事务会查最小并发数
		producer.setCheckThreadPoolMinSize(2);
		//事务会查最大并发数
		producer.setCheckThreadPoolMaxSize(2);
		//队列数
		producer.setCheckRequestHoldMax(2000);
		producer.start();
		logger.info("rocketmq transaction producer server is starting...");
		return producer;
	}
	
	public DefaultMQPushConsumer publicConsumer() throws MQClientException{
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketMQProperties.getConsumerGroupName());
		consumer.setNamesrvAddr(rocketMQProperties.getNamesrvAddr());
		consumer.setInstanceName(rocketMQProperties.getConsumerInstanceName());
		//判断是否为广播
		if (rocketMQProperties.isConsumerBroadcasting()) {
			consumer.setMessageModel(MessageModel.BROADCASTING);
		}
		//批量设置消费
		consumer.setConsumeMessageBatchMaxSize(rocketMQProperties.getConsumerBatchMaxSize() == 0? 1 : rocketMQProperties.getConsumerBatchMaxSize());
		
		//获取topic和tag
		List<String> subscribeList = rocketMQProperties.getSubscribe();
		for (String subscribe : subscribeList) {
			consumer.subscribe(subscribe.split(":")[0], subscribe.split(":")[1]);
		}
		
		//顺序消费
		if (rocketMQProperties.isEnableOrderConsumer()) {
			consumer.registerMessageListener(new MessageListenerOrderly() {
				
				@Override
				public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
					try {
						context.setAutoCommit(true);
						msgs = filterMessage(msgs);
						if (msgs.size() == 0) {
							return ConsumeOrderlyStatus.SUCCESS;
						}
						publisher.publishEvent(new RocketMQMessageEvent(msgs, consumer));
					} catch (Exception e) {
						e.printStackTrace();
						return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
					}
					return ConsumeOrderlyStatus.SUCCESS;
				}
			});
		}else {//并发消费
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
					try {
						msgs = filterMessage(msgs);
						if (msgs.size() == 0) {
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						}
						publisher.publishEvent(new RocketMQMessageEvent(null, consumer));
					} catch (Exception e) {
						e.printStackTrace();
						return ConsumeConcurrentlyStatus.RECONSUME_LATER;
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					consumer.start();
					logger.info("rocketmq consumer server is starting...");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MQClientException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return consumer;
	}
	
	/**
   	  *  消息过滤
	 * @param msgs
	 * @return
	 */
	private List<MessageExt> filterMessage(List<MessageExt> msgs){
		if (isFirstSub && !rocketMQProperties.isEnableHistoryConsumer()) {
			msgs = msgs.stream().filter(item -> startTime - item.getBornTimestamp() < 0).collect(Collectors.toList());
		}
		if (isFirstSub && msgs.size() > 0) {
			isFirstSub = false;
		}
		return msgs;
	}
}
