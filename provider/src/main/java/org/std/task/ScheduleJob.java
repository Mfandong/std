package org.std.task;

public class ScheduleJob {
	
	private String jobName;
	private String jobId;
	private String jobClass;
	private String jobCron;
	
	public ScheduleJob() {
	}
	public ScheduleJob(String jobName, String jobId, String jobClass, String jobCron) {
		super();
		this.jobName = jobName;
		this.jobId = jobId;
		this.jobClass = jobClass;
		this.jobCron = jobCron;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getJobClass() {
		return jobClass;
	}
	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}
	public String getJobCron() {
		return jobCron;
	}
	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}
}
