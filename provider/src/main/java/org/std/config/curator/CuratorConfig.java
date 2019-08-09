package org.std.config.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix="curator", ignoreInvalidFields=true, ignoreUnknownFields=true)
public class CuratorConfig {
	/** zk连接地址 */
	private String connectString;
	/** 重试次数 */
	private String retryCount;
	/** 重试间隔时间 */
	private String elapsedTimeMs;
	/** session超时时间 */
	private String sessionTimeoutMs;
	/** 连接超时时间 */
	private String connectionTimeoutMs;
	
	@Bean(initMethod = "start")  //bean初始化要执行的方法
	public CuratorFramework curatorFramework(){
		
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
				.connectString(connectString)
				.sessionTimeoutMs(Integer.parseInt(sessionTimeoutMs))
				.connectionTimeoutMs(Integer.parseInt(connectionTimeoutMs))
				.retryPolicy(retryPolicy)
				.namespace("curator")  //创建命名空间即根目录
				.build();
		return curatorFramework;
//		return CuratorFrameworkFactory.newClient(connectString, 
//				Integer.parseInt(sessionTimeoutMs), 
//				Integer.parseInt(connectionTimeoutMs), 
//				new RetryNTimes(Integer.parseInt(retryCount), Integer.parseInt(elapsedTimeMs)));
	}
	
	public String getConnectString() {
		return connectString;
	}
	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}
	public String getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(String retryCount) {
		this.retryCount = retryCount;
	}
	public String getElapsedTimeMs() {
		return elapsedTimeMs;
	}
	public void setElapsedTimeMs(String elapsedTimeMs) {
		this.elapsedTimeMs = elapsedTimeMs;
	}
	public String getSessionTimeoutMs() {
		return sessionTimeoutMs;
	}
	public void setSessionTimeoutMs(String sessionTimeoutMs) {
		this.sessionTimeoutMs = sessionTimeoutMs;
	}
	public String getConnectionTimeoutMs() {
		return connectionTimeoutMs;
	}
	public void setConnectionTimeoutMs(String connectionTimeoutMs) {
		this.connectionTimeoutMs = connectionTimeoutMs;
	}
}
