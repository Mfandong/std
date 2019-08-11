package org.std.task;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class QuartzService implements ApplicationRunner{
	
	private static final String DEFAULT_JOB_GROUP_NAME = "DEFAULT_JOB_GROUP_NAME";
	
	private static final String DEFAULT_TRIGGER_GROUP_NAME = "DEFAULT_TRIGGER_GROUOP_NAME";
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private TaskService taskService;


	/**
	 * Springboot启动加载执行
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<ScheduleJob> jobList = taskService.getJobList();
		jobList.forEach(item -> {
			try {
				addJob(item);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	/**
	 * 添加任务
	 * @param job
	 * @param jobName
	 * @throws SchedulerException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	public void addJob(ScheduleJob job) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (job == null) {
			return;
		}
		String jobName = job.getJobName();
		
		if (!scheduler.isStarted()) {
			scheduler.start();
		}
		
		JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, DEFAULT_JOB_GROUP_NAME));
		if (jobDetail == null) {
			Class clazz = Class.forName(job.getJobClass());
			jobDetail = JobBuilder.newJob(((Job)clazz.newInstance()).getClass())
					              .withIdentity(jobName, DEFAULT_JOB_GROUP_NAME)
					              .build();
			jobDetail.getJobDataMap().put("jobName", jobName);
			CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(job.getJobCron());
			Trigger trigger = TriggerBuilder.newTrigger()
											.withIdentity(jobName, DEFAULT_TRIGGER_GROUP_NAME)
											.withSchedule(cronSchedule)
											.build();
			scheduler.scheduleJob(jobDetail, trigger);
		}
	}
	
	/**
	 * 删除任务
	 * @param jobName
	 * @throws SchedulerException
	 */
	public void removeJob(String jobName) throws SchedulerException {
		scheduler.pauseTrigger(TriggerKey.triggerKey(jobName, DEFAULT_TRIGGER_GROUP_NAME));
		scheduler.unscheduleJob(TriggerKey.triggerKey(jobName, DEFAULT_JOB_GROUP_NAME));
		scheduler.deleteJob(JobKey.jobKey(jobName, DEFAULT_JOB_GROUP_NAME));
	}
	
	/**
	 * 检查任务是否存在
	 * @param jobName
	 * @return
	 * @throws SchedulerException
	 */
	public boolean checkExist(String jobName) throws SchedulerException {
		JobDetail jobDetail = scheduler.getJobDetail(JobKey.jobKey(jobName, DEFAULT_JOB_GROUP_NAME));
		if (jobDetail != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 更新任务
	 * @param job
	 * @throws SchedulerException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void updateJob(ScheduleJob job) throws SchedulerException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String jobName = job.getJobName();
		boolean checkExist = checkExist(jobName);
		if (checkExist) {
			removeJob(jobName);
		}
		addJob(job);
	}
}
