package org.std.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TimingTask {

	@Scheduled(cron = "0 0/1 * * * ? ")
	public void timeTask() throws InterruptedException {
		System.out.println("注解实现定时任务执行开始。。。");
		Thread.sleep(3000);
		System.out.println("注解实现定时任务执行结束。。。");
	}
}
