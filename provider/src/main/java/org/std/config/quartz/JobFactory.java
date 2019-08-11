package org.std.config.quartz;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

@Component
public class JobFactory extends AdaptableJobFactory{
	@Autowired
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		//调用父类方法
		Object jobInstance = super.createJobInstance(bundle);
		//进行注入
		autowireCapableBeanFactory.autowireBean(jobInstance);
		
		return jobInstance;
	}
}
