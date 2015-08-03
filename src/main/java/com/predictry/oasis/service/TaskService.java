package com.predictry.oasis.service;

import java.util.List;

import javax.validation.Valid;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.SchedulerOperations;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.repository.TaskRepository;

/**
 * This class provides operation to manage <code>Task</code>.
 * 
 * @author jocki
 *
 */
@Service
public class TaskService {

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private TaskRepository taskRepository;
	
	public Task findById(Long id) {
		return taskRepository.findOne(id);
	}
	
	public List<Task> list() throws SchedulerException {
//		List<TriggerDisplayModel> results = new ArrayList<>();
//		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
//			SchedulerConfig.QUARTZ_SP_INVOKE_REST_JOB_GROUP));
//		for (TriggerKey triggerKey: triggerKeys) {
//			TriggerDisplayModel triggerModel = new TriggerDisplayModel(scheduler.getTrigger(triggerKey));
//			TriggerState state = scheduler.getTriggerState(triggerKey);
//			triggerModel.setStatus(state.toString());
//			results.add(triggerModel);
//		}
//		return results;
		return taskRepository.findAll();
	}
	
	public void add(@Valid Task task) throws SchedulerException {
		boolean addNew = (task.getId() == null);
		task = taskRepository.save(task);
		if (addNew) {
			SchedulerOperations schedulerOperations = new SchedulerOperations(scheduler, task);
			schedulerOperations.schedule(scheduler);
		}
	}
	
	public void delete(Long id) throws SchedulerException {
		Task task = taskRepository.findOne(id);
		if (task != null) {
			SchedulerOperations schedulerOperations = new SchedulerOperations(scheduler, task);
			schedulerOperations.removeSchedule(scheduler);
			taskRepository.delete(task);
		}
	}
	
}
