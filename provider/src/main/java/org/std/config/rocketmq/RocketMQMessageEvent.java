package org.std.config.rocketmq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.ApplicationEvent;
/**
  *  监听对象 
 */
public class RocketMQMessageEvent extends ApplicationEvent{
	private static final long serialVersionUID = -3463806216851855859L;

	private DefaultMQPushConsumer consumer;
	private List<MessageExt> msgs;
	public RocketMQMessageEvent(List<MessageExt> msgs, DefaultMQPushConsumer consumer) {
		super(msgs);
		this.consumer = consumer;
		this.setMsgs(msgs);
	}
	public DefaultMQPushConsumer getConsumer() {
		return consumer;
	}
	public void setConsumer(DefaultMQPushConsumer consumer) {
		this.consumer = consumer;
	}
	public List<MessageExt> getMsgs() {
		return msgs;
	}
	public void setMsgs(List<MessageExt> msgs) {
		this.msgs = msgs;
	}
}
