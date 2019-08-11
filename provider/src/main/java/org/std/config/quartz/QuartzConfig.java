package org.std.config.quartz;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {
	@Autowired
	private JobFactory jobFactory;
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() {
		SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
		schedulerFactoryBean.setJobFactory(jobFactory);
		//用于quartz集群，QuartzScheduler启动时更新已存在的job
		schedulerFactoryBean.setOverwriteExistingJobs(true);
		schedulerFactoryBean.setStartupDelay(1);
		return schedulerFactoryBean;
	}
	
	@Bean
	public Scheduler scheduler() {
		return schedulerFactoryBean().getScheduler();
	}
}
