package org.std.service;

public interface IRocketMQService {
	
	public void sendMsg();
	
	public String sendTransactionMsg();
	
	public void sendMsgOrder();
}
