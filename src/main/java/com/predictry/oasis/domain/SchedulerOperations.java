package com.predictry.oasis.domain;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

import java.util.Set;

import org.apache.http.util.Asserts;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.predictry.oasis.config.SchedulerConfig;

/**
 * This class contains operations that can be performed by Quartz Scheduler
 * on certain <code>Task</code>.
 * 
 * @author stewart
 *
 */
public class SchedulerOperations {

	private static final Logger LOG = LoggerFactory.getLogger(SchedulerOperations.class);
	
	private Scheduler scheduler;
	private Application app;
	
	public SchedulerOperations(Scheduler scheduler, Application app) {
		this.scheduler = scheduler;
		this.app = app;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Application getTask() {
		return app;
	}

	public void setTask(Application app) {
		this.app = app;
	}
	
	public void schedule() throws SchedulerException {
		Asserts.notNull(app.getId(), "Id can't be null!");
		Asserts.notNull(app.getName(), "Name can't be empty!");
		Asserts.notNull(app.getCron(), "Cron expression can't be empty!");
		Trigger trigger = newTrigger()
			.withIdentity(app.getName(), SchedulerConfig.QUARTZ_APP_GROUP)
			.withSchedule(cronSchedule(app.getCron()))
			.forJob(SchedulerConfig.QUARTZ_APP_GROUP, 
					SchedulerConfig.QUARTZ_APP_GROUP)
			.usingJobData("app.id", String.valueOf(app.getId()))
			.build();
		scheduler.scheduleJob(trigger);
	}	
	
	public void removeSchedule() throws SchedulerException {
		Asserts.notNull(app.getId(), "Id can't be null!");
		Asserts.notNull(app.getName(), "Name can't be empty!");
		Asserts.notNull(app.getCron(), "Cron expression can't be empty!");
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
			SchedulerConfig.QUARTZ_APP_GROUP));
		triggerKeys.stream()
			.filter(t -> t.getName().equals(app.getName()))
			.forEach(t -> {
				try {
					scheduler.unscheduleJob(t);
				} catch (Exception e) {
					LOG.error("Error while unscheduling app job [" + t.getName() + "]", e);
				}
			});
	}
	
}
