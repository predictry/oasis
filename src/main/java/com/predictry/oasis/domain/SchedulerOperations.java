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

import com.predictry.oasis.config.SchedulerConfig;

/**
 * This class contains operations that can be performed by Quartz Scheduler
 * on certain <code>Task</code>.
 * 
 * @author stewart
 *
 */
public class SchedulerOperations {

	private Scheduler scheduler;
	private Task task;
	
	public SchedulerOperations(Scheduler scheduler, Task task) {
		this.scheduler = scheduler;
		this.task = task;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
	
	public void schedule(Scheduler scheduler) throws SchedulerException {
		Asserts.notNull(task.getId(), "Id can't be null!");
		Asserts.notNull(task.getName(), "Name can't be empty!");
		Asserts.notNull(task.getCron(), "Cron expression can't be empty!");
		Trigger trigger = newTrigger()
			.withIdentity(task.getName(), SchedulerConfig.QUARTZ_SP_INVOKE_REST_JOB_GROUP)
			.withSchedule(cronSchedule(task.getCron()))
			.forJob(SchedulerConfig.QUARTZ_SP_INVOKE_REST_JOB_GROUP, 
					SchedulerConfig.QUARTZ_SP_INVOKE_REST_JOB_GROUP)
			.usingJobData("task.id", String.valueOf(task.getId()))
			.build();
		scheduler.scheduleJob(trigger);
	}	
	
	public void removeSchedule(Scheduler scheduler) throws SchedulerException {
		Asserts.notNull(task.getId(), "Id can't be null!");
		Asserts.notNull(task.getName(), "Name can't be empty!");
		Asserts.notNull(task.getCron(), "Cron expression can't be empty!");
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
			SchedulerConfig.QUARTZ_SP_INVOKE_REST_JOB_GROUP));
		for (TriggerKey triggerKey: triggerKeys) {
			if (triggerKey.getName().equals(task.getName())) {
				scheduler.unscheduleJob(triggerKey);
				break;
			}
		}
	}
	
}
