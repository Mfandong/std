package org.std.rocketmq;

public class RocketMqMessage {
	
	private String id;
	private String name;
	private String message;
	public RocketMqMessage() {
	}
	public RocketMqMessage(String id, String name, String message) {
		super();
		this.id = id;
		this.name = name;
		this.message = message;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
