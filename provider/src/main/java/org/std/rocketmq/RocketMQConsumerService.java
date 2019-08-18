package org.std.rocketmq;

import java.util.List;

import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.std.config.rocketmq.RocketMQMessageEvent;

/**
  * 监听消息进行消费
 *
 */
@Component
public class RocketMQConsumerService {
	
	@EventListener(condition = "#event.msgs[0].topic='std-topic' && #event.msgs[0].tags='std-tag'")
	public void rocketMQMsgListener(RocketMQMessageEvent event) {
		try {
			List<MessageExt> msgs = event.getMsgs();
			for (MessageExt msg : msgs) {
				System.out.println("消费消息：" + new String(msg.getBody()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
