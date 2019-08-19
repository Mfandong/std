package org.std.config.rocketmq;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
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
	private ApplicationEventPublisher publisher;
	
	private static boolean isFirstSub = true;
	
	private static long startTime = System.currentTimeMillis();
	
	private static Logger logger = LoggerFactory.getLogger(RocketMQConfiguration.class);
	@Bean(name="defaultMQProducer")
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
	
	@Bean(name="transactionMQProducer")
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
		producer.setTransactionCheckListener(new TransactionCheckListenerImpl());
		producer.start();
		logger.info("rocketmq transaction producer server is starting...");
		return producer;
	}
	
	@Bean
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
						publisher.publishEvent(new RocketMQMessageEvent(msgs, consumer));
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
	
	class TransactionCheckListenerImpl implements TransactionCheckListener {
		private AtomicInteger transactionIndex = new AtomicInteger(1);
		@Override
		public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
			System.out.println("server checking TrMsg " + msg.toString());

			return checkIsCommitLocal();
		}
		
		/**
		 * rocketMQ消息回查，判断本地事务是否提交成功
		 * @return
		 */
		private LocalTransactionState checkIsCommitLocal(){
			int value = transactionIndex.getAndIncrement();
			System.out.println("[TransactionCheckListenerImpl] --> " + value);
			//如果5次确认查询本地事务都没有提交成功，则回滚消息
			if ((value % 5) == 0){
				return LocalTransactionState.ROLLBACK_MESSAGE;
			}
			
			//查询数据库，判断库中是否存在该记录；模拟三次确认查询成功
			if((value % 3) == 0){
				return LocalTransactionState.COMMIT_MESSAGE;
			}
			
			//数据库中没有记录，并且没有超出确认次数，则返回UNKNOW
			return LocalTransactionState.UNKNOW;
		}
		
	}
}
