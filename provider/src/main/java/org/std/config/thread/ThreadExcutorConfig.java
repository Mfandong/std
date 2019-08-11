package org.std.config.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Async
public class ThreadExcutorConfig {

	@Bean
	public Executor asyncServiceExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
		//配置队列等待线程数大小
		executor.setQueueCapacity(5000);
		executor.setKeepAliveSeconds(60);
		//当pool已经达到max size的时候，不在当前线程中执行，由调用者执行
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		
		return executor;
	}
}
