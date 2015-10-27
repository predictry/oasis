package com.predictry.oasis.service;

import java.io.IOException;
import java.util.List;

import javax.script.ScriptEngineManager;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.SchedulerOperations;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.repository.ApplicationRepository;
import com.predictry.oasis.repository.JobRepository;
import com.predictry.oasis.util.JsonMessageCreator;

/**
 * This class provides operation to manage <code>Application</code>.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class ApplicationService {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationService.class);
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired @Qualifier("queue")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ApplicationRepository appRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ScriptEngineManager scriptEngineManager;
	
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
	
	public void execute(Long id) throws JsonParseException, JsonMappingException, IOException {
		Application app = appRepository.findOne(id);
		if (app != null) {
			Job job = app.execute(objectMapper, scriptEngineManager);
			if (job != null) {
				jmsTemplate.send(app.getQueueName(), new JsonMessageCreator(objectMapper, job.getPayloadAsMap()));
				jobRepository.save(job);
			}
		} else {
			LOG.warn("Can't find application instance for id [" + id + "]");
		}
	}
	
//	@JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.STATUS")
//	public void receiveStatus(Map<String, Object> map) throws JMSException {
//		LOG.info("Receiving status [" + map + "]");
//		if (!map.containsKey("serviceProvider") || !map.containsKey("jobId")) {
//			LOG.warn("Encountered invalid message, it will be ignored!");
//			return;
//		}
//		String serviceProvider = (String) map.get("serviceProvider");
//		List<Application> apps = appRepository.findByServiceProviderName(serviceProvider);
//		for (Application app: apps) {
//			for (StatusHandler handler: statusHandlers) {
//				handler.handle(app, map);
//			}
//		}
//	}
	
}
