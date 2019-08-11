package org.std.task;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

@Service
public class MyJobService implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SimpleDateFormat sdf = new SimpleDateFormat();
		Object jobName = context.getJobDetail().getJobDataMap().get("jobName");
		System.out.println(sdf.format(new Date()) + "-->" + jobName + "-->【MyJobService】处理业务逻辑开始。。。");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(sdf.format(new Date()) + "-->" + jobName + "-->【MyJobService】处理业务逻辑逻结束。。。");
	}

}
