package org.std.task;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

/**
 * Schedule 的job服务
 * 通过list来模拟数据库数据 
 */
@Service
public class TaskService {

	private List<ScheduleJob> jobList = new ArrayList<ScheduleJob>();
	
	/**
	 * 模拟初始化一条数据，实际应用可以直接读取库中的记录
	 * 该注解时在构造函数之后初始化方法之前执行
	 */
	@PostConstruct
	public void init() {
		ScheduleJob job = new ScheduleJob();
		job.setJobId("1");
		job.setJobName("测试定时任务");
		job.setJobClass("org.std.task.MyJobService");
		job.setJobCron("0 0/1 * * * ? ");
		jobList.add(job);
	}
	
	public List<ScheduleJob> getJobList(){
		return jobList;
	}
	
	public void addJobList(ScheduleJob job) {
		jobList.add(job);
	}
	
	public void removeJobList(ScheduleJob Job) {
		jobList.remove(Job);
	}
	
	public void clearJobList() {
		jobList.clear();
	}
}
