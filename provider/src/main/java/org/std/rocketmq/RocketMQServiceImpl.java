package org.std.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.std.service.IRocketMQService;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

@Service(version = "1.0", interfaceClass = IRocketMQService.class)
@Component
public class RocketMQServiceImpl implements IRocketMQService{

	@Autowired
	private DefaultMQProducer producer;
	@Autowired
	private TransactionMQProducer transactionProducer;
	@Override
	public void sendMsg() {
		for (int i = 0; i < 10; i++) {
			RocketMqMessage rocketMqMessage = new RocketMqMessage(i+"", "消息"+i, "RocketMQ发送消息"+i);
			String messageJson = JSON.toJSONString(rocketMqMessage);
			Message message = new Message("message-topic", "message-tag", messageJson.getBytes());
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
			//a、b、c三个值对于三个不同的状态
			String ms = "c";
			Message msg = new Message("std-topic", "std-tag", ms.getBytes());
			//发送事务消息
			sendResult = transactionProducer.sendMessageInTransaction(msg, new LocalTransactionExecuter() {
				
				@Override
				public LocalTransactionState executeLocalTransactionBranch(Message msg, Object arg) {
					String value = "";
					if (arg instanceof String) {
						value = (String)arg;
					}
					if (value.equals("")) {
						throw new RuntimeException("发送消息不能为空...");
					}else if (value.equals("a")) {
						return LocalTransactionState.ROLLBACK_MESSAGE;
					}else if (value.equals("b")) {
						return LocalTransactionState.COMMIT_MESSAGE;
					}
					return LocalTransactionState.ROLLBACK_MESSAGE;
				}
			}, 4);
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
			Message msg = new Message("message-topic", "message-tag", messageJson.getBytes());
			try {
				producer.send(msg, new MessageQueueSelector() {
					
					@Override
					public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
						int index = ((Integer) arg) % mqs.size();
						return mqs.get(index);
					}
				}, i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
