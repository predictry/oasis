package com.predictry.oasis.service;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.SchedulerOperations;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.repository.ApplicationRepository;

/**
 * This class provides operation to manage <code>Application</code>.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class ApplicationService {

	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private ApplicationRepository appRepository;
	
	public Application findById(Long id) {
		return appRepository.findOne(id);
	}
	
	public List<Application> list() throws SchedulerException {
		return appRepository.findAll();
	}
	
	public void add(@Valid Application app) throws SchedulerException {
		boolean addNew = (app.getId() == null);
		app = appRepository.save(app);
		if (addNew) {
			SchedulerOperations schedulerOperations = new SchedulerOperations(scheduler, app);
			schedulerOperations.schedule(scheduler);
		}
	}
	
	public void delete(Application app) throws SchedulerException {
		if (app != null) {
			SchedulerOperations schedulerOperations = new SchedulerOperations(scheduler, app);
			schedulerOperations.removeSchedule(scheduler);
			appRepository.delete(app);
			appRepository.flush();
		}
	}
	
	public Application addTask(Application app, Task task) {
		app = appRepository.findOne(app.getId());
		app.addTask(task);
		return app;
	}
	
	public Application editTask(Application app, Task task, int index) {
		app = appRepository.findOne(app.getId());
		app.editTask(task, index);
		return app;
	}
	
	public Application deleteTask(Application app, int index) {
		app = appRepository.findOne(app.getId());
		app.deleteTask(index);
		return app;
	}
	
}
