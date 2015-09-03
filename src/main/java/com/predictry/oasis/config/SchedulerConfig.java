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
import com.predictry.oasis.job.ApplicationJob;

/**
 * Configuration for Quartz Scheduler.
 * 
 * @author jocki
 *
 */
@Configuration
public class SchedulerConfig {

	public static final String QUARTZ_HEARTBEAT_JOB_GROUP = "HEARTBEAT_GROUP";
	public static final String QUARTZ_HEARTBEAT_JOB_NAME = "HEARTBEAT_JOB";
	public static final int QUARTZ_HEARTBEAT_JOB_INTERVAL = 5 * 60 * 1000; // 5 minutes
	
	public static final String QUARTZ_APP_GROUP = "APP_GROUP";
	
	@Autowired
	private DataSource dataSource;
	
	/**
	 * Bean definition to execute <code>hearbeatService.heartbeat()</code> 
	 * periodically.
	 * 
	 * @return <code>JobDetailFactoryBean</code>.
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
	 * Bean definition to execute REST request.
	 * 
	 * @return <code>JobDetailFactoryBean</code>.
	 */
	@Bean
	public JobDetailFactoryBean applicationJobFactory() {
		JobDetailFactoryBean invokeRestJobFactory =
			new JobDetailFactoryBean();
		invokeRestJobFactory.setJobClass(ApplicationJob.class);
		invokeRestJobFactory.setGroup(QUARTZ_APP_GROUP);
		invokeRestJobFactory.setName(QUARTZ_APP_GROUP);
		invokeRestJobFactory.setDurability(true);
		return invokeRestJobFactory;
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
		props.put("org.quartz.jobStore.useProperties", "true");
		schedulerFactory.setQuartzProperties(props);
		schedulerFactory.setTriggers(heartbeatTriggerFactory().getObject());
		schedulerFactory.setJobDetails(applicationJobFactory().getObject());
		return schedulerFactory;
	}
	
}
