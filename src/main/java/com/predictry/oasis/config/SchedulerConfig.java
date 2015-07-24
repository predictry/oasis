package com.predictry.oasis.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.predictry.oasis.job.HeartbeatJob;

@Configuration
public class SchedulerConfig {

	public static final String QUARTZ_HEARTBEAT_JOB_GROUP = "HEARTBEAT_GROUP";
	public static final String QUARTZ_HEARTBEAT_JOB_NAME = "HEARTBEAT_JOB";
	public static final int QUARTZ_HEARTBEAT_JOB_INTERVAL = 5 * 60 * 1000; // 5 minutes
	
	public static final String QUARTZ_SP_JOB_GROUP = "SP_GROUP";
	
	@Autowired
	private DataSource dataSource;
	
	/**
	 * Bean definition to execute <code>hearbeatService.heartbeat()</code> 
	 * periodically.
	 * 
	 * @return <code>MethodInvokingJobDetailFactoryBean</code>.
	 */
	@Bean
	public JobDetailFactoryBean heartbeatJobFactory() {
		JobDetailFactoryBean heartbeatJobFactory = 
			new JobDetailFactoryBean();
		heartbeatJobFactory.setJobClass(HeartbeatJob.class);
		heartbeatJobFactory.setGroup(QUARTZ_HEARTBEAT_JOB_GROUP);
		heartbeatJobFactory.setName(QUARTZ_HEARTBEAT_JOB_NAME);
		heartbeatJobFactory.setDurability(true);
		return heartbeatJobFactory;
	}
	
	/**
	 * Bean definition for Quartz Trigger that executes heartbeat
	 * service.
	 * 
	 * @return <code>SimpleTriggerFactoryBean</code>.
	 */
	@Bean
	public SimpleTriggerFactoryBean heartbeatTriggerFactory() {
		SimpleTriggerFactoryBean heartbeatTriggerFactory =
			new SimpleTriggerFactoryBean();
		heartbeatTriggerFactory.setJobDetail(heartbeatJobFactory().getObject());
		heartbeatTriggerFactory.setRepeatInterval(QUARTZ_HEARTBEAT_JOB_INTERVAL);
		return heartbeatTriggerFactory;
	}
	
	/**
	 * Bean definition for creating Quartz scheduler instance.
	 *  
	 * @return <code>SchedulerFactoryBean</code>.
	 */
	@Bean
	public SchedulerFactoryBean scheduler() {
		SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
		schedulerFactory.setDataSource(dataSource);
		schedulerFactory.setSchedulerName("OMSQuartzScheduler");
		Properties props = new Properties();
		props.put("org.quartz.threadPool.threadCount", "10");
		props.put("org.quartz.threadPool.threadPriority", "5");
		props.put("org.quartz.jobStore.misfireThreshold", "60000");
		schedulerFactory.setQuartzProperties(props);
		schedulerFactory.setTriggers(heartbeatTriggerFactory().getObject());
		return schedulerFactory;
	}
	
}
