package org.std.rocketmq;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.stereotype.Component;
import org.std.service.IRocketMQService;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
@Service(version="1.0.0", interfaceClass=IRocketMQService.class)
@Component
public class RocketMQServiceImpl implements IRocketMQService{

	@Resource(name="defaultMQProducer")
	private DefaultMQProducer producer;
	@Resource(name="transactionMQProducer")
	private TransactionMQProducer transactionProducer;
	@Override
	public void sendMsg() {
		for (int i = 0; i < 10; i++) {
			RocketMqMessage rocketMqMessage = new RocketMqMessage(i+"", "消息"+i, "RocketMQ发送消息"+i);
			String messageJson = JSON.toJSONString(rocketMqMessage);
			Message message = new Message("std-topic", "std-tag", messageJson.getBytes());
			SendResult result;
			try {
				result = producer.send(message);
				System.out.println("发送响应：MsgId：" + result.getMsgId() + "，发送状态：" + result.getSendStatus());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String sendTransactionMsg() {
		SendResult sendResult = null;
		try {
			RocketMqMessage rocketMqMessage = new RocketMqMessage("1101", "事务消息", "发送事务消息");
			String messageJson = JSON.toJSONString(rocketMqMessage);
			System.out.println("[sendTransactionMsg] --> 发送事务消息：" + messageJson);
			Message msg = new Message("std-topic", "std-tag", messageJson.getBytes());
			//发送事务消息
			sendResult = transactionProducer.sendMessageInTransaction(msg, new LocalTransactionExecuter() {
				@Override
				public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
					System.out.println("[executeLocalTransactionBranch] --> 发送事务消息：" + new String(msg.getBody()));
					return LocalTransactionState.UNKNOW;
				}
			}, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sendResult.toString();
	}

	//支持顺序发送消息
	@Override
	public void sendMsgOrder() {
		for (int i = 0; i < 10; i++) {
			RocketMqMessage rocketMqMessage = new RocketMqMessage(i+"", "消息"+i, "RocketMQ发送消息"+i);
			String messageJson = JSON.toJSONString(rocketMqMessage);
			System.out.println("[sendMsgOrder] --> 发送顺序消息：" + messageJson);
			Message msg = new Message("std-topic", "std-tag", messageJson.getBytes());
			try {
				producer.send(msg, new MessageQueueSelector() {
					
					@Override
					public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
						int index = ((Integer) arg) % mqs.size();
						System.out.println("[sendMsgOrder] --> 发送顺序消息：" + index);
						return mqs.get(index);
					}
				}, i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
