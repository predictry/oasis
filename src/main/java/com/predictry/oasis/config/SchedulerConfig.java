package com.predictry.oasis.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import com.predictry.oasis.job.ApplicationJob;
import com.predictry.oasis.job.EC2TerminatorJob;
import com.predictry.oasis.job.HeartbeatJob;
import com.predictry.oasis.job.MetricJob;

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
	
	public static final String QUARTZ_METRIC_JOB_GROUP = "METRIC_GROUP";
	public static final String QUARTZ_METRIC_JOB_NAME = "METRIC_JOB";
	public static final int QUARTZ_METRIC_JOB_INTERVAL = 60 * 60 * 1000; // 1 hour
	
	public static final String QUARTZ_KEEPER_JOB_GROUP = "KEEPER_GROUP";
	public static final String QUARTZ_KEEPER_JOB_NAME = "KEEPER_JOB";
	public static final int QUARTZ_KEEPER_JOB_INTERVAL =  3 * 60 * 1000; // 3 hours
	
	public static final String QUARTZ_EC2_TERMINATOR_GROUP = "EC2_TERMINATOR_GROUP";
	public static final String QUARTZ_EC2_TERMINATOR_NAME = "EC2_TERMINATOR_JOB";
	public static final int QUARTZ_EC2_JOB_INTERVAL =  30 * 60 * 1000; // 30 minutes
	
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
	 * Bean definition to execute metric job periodically.
	 * 
	 * @return <code>JobDetailFactoryBean</code>.
	 */
	@Bean
	public JobDetailFactoryBean metricJobFactory() {
		JobDetailFactoryBean metricJobFactory = new JobDetailFactoryBean();
		metricJobFactory.setJobClass(MetricJob.class);
		metricJobFactory.setGroup(QUARTZ_METRIC_JOB_GROUP);
		metricJobFactory.setName(QUARTZ_METRIC_JOB_NAME);
		metricJobFactory.setDurability(true);
		return metricJobFactory;
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
	 * Bean definition to terminate EC2.
	 * 
	 * @return <code>JobDetailFactoryBean</code>.
	 */
	@Bean
	public JobDetailFactoryBean ec2TerminatorJobFactory() {
		JobDetailFactoryBean ec2TerminatorJobFactory = 
			new JobDetailFactoryBean();
		ec2TerminatorJobFactory.setJobClass(EC2TerminatorJob.class);
		ec2TerminatorJobFactory.setGroup(QUARTZ_EC2_TERMINATOR_GROUP);
		ec2TerminatorJobFactory.setName(QUARTZ_EC2_TERMINATOR_NAME);
		ec2TerminatorJobFactory.setDurability(true);
		return ec2TerminatorJobFactory;
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
	 * Bean definition for Quartz Trigger that executes metric service.
	 * 
	 * @return <code>SimpleTriggerFactoryBean</code>.
	 */
	@Bean
	public SimpleTriggerFactoryBean metricTriggerFactory() {
		SimpleTriggerFactoryBean metricTriggerFactory = new SimpleTriggerFactoryBean();
		metricTriggerFactory.setJobDetail(metricJobFactory().getObject());
		metricTriggerFactory.setRepeatInterval(QUARTZ_METRIC_JOB_INTERVAL);
		return metricTriggerFactory;
	}
	
	/**
	 * Bean definition for EC2 terminator service.
	 * 
	 * @return <code>CronTriggerFactoryBean</code>.
	 */
	@Bean
	public SimpleTriggerFactoryBean ec2TerminatorTriggerFactory() {
		SimpleTriggerFactoryBean cronTriggerFactory = new SimpleTriggerFactoryBean();
		cronTriggerFactory.setJobDetail(ec2TerminatorJobFactory().getObject());
		cronTriggerFactory.setRepeatInterval(QUARTZ_EC2_JOB_INTERVAL);
		return cronTriggerFactory;
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
		schedulerFactory.setTriggers(heartbeatTriggerFactory().getObject(), metricTriggerFactory().getObject(), 
			ec2TerminatorTriggerFactory().getObject());
		schedulerFactory.setJobDetails(applicationJobFactory().getObject(), metricJobFactory().getObject(), 
			ec2TerminatorJobFactory().getObject());
		return schedulerFactory;
	}
	
}
