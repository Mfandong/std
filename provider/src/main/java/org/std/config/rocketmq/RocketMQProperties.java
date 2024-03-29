package org.std.config.rocketmq;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:rocketmq.properties")
@ConfigurationProperties(prefix = "std.rocketmq")
@Configuration
public class RocketMQProperties {

	private String namesrvAddr;
	private String producerGroupName;
	private String consumerGroupName;
	private String transactionProducerGroupName;
	private String producerInstanceName;
	private String consumerInstanceName;
	private String producerTranInstanceName;
	private int consumerBatchMaxSize;
	private boolean consumerBroadcasting;
	private boolean enableHistoryConsumer;
	private boolean enableOrderConsumer;
	private List<String> subscribe = new ArrayList<String>();
	public String getNamesrvAddr() {
		return namesrvAddr;
	}
	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}
	public String getProducerGroupName() {
		return producerGroupName;
	}
	public void setProducerGroupName(String producerGroupName) {
		this.producerGroupName = producerGroupName;
	}
	public String getConsumerGroupName() {
		return consumerGroupName;
	}
	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
	}
	public String getTransactionProducerGroupName() {
		return transactionProducerGroupName;
	}
	public void setTransactionProducerGroupName(String transactionProducerGroupName) {
		this.transactionProducerGroupName = transactionProducerGroupName;
	}
	public String getProducerInstanceName() {
		return producerInstanceName;
	}
	public void setProducerInstanceName(String producerInstanceName) {
		this.producerInstanceName = producerInstanceName;
	}
	public String getConsumerInstanceName() {
		return consumerInstanceName;
	}
	public void setConsumerInstanceName(String consumerInstanceName) {
		this.consumerInstanceName = consumerInstanceName;
	}
	public String getProducerTranInstanceName() {
		return producerTranInstanceName;
	}
	public void setProducerTranInstanceName(String producerTranInstanceName) {
		this.producerTranInstanceName = producerTranInstanceName;
	}
	public int getConsumerBatchMaxSize() {
		return consumerBatchMaxSize;
	}
	public void setConsumerBatchMaxSize(int consumerBatchMaxSize) {
		this.consumerBatchMaxSize = consumerBatchMaxSize;
	}
	public boolean isConsumerBroadcasting() {
		return consumerBroadcasting;
	}
	public void setConsumerBroadcasting(boolean consumerBroadcasting) {
		this.consumerBroadcasting = consumerBroadcasting;
	}
	public boolean isEnableHistoryConsumer() {
		return enableHistoryConsumer;
	}
	public void setEnableHistoryConsumer(boolean enableHistoryConsumer) {
		this.enableHistoryConsumer = enableHistoryConsumer;
	}
	public boolean isEnableOrderConsumer() {
		return enableOrderConsumer;
	}
	public void setEnableOrderConsumer(boolean enableOrderConsumer) {
		this.enableOrderConsumer = enableOrderConsumer;
	}
	public List<String> getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(List<String> subscribe) {
		this.subscribe = subscribe;
	}
}
