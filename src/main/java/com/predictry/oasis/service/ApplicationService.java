package com.predictry.oasis.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.SchedulerOperations;
import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.repository.ApplicationRepository;
import com.predictry.oasis.repository.ServiceProviderRepository;

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
	
	@Autowired
	private ApplicationRepository appRepository;
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Value("classpath:default_compute_recommendation_task.json")
	private Resource defaultComputeRecommendationPayload;
	
	@Value("classpath:default_import_record_task.json")
	private Resource defaultImportRecordPayload;
	
	public Application findById(Long id) {
		return appRepository.findOne(id);
	}
	
	public List<Application> list() throws SchedulerException {
		return appRepository.findAll();
	}
	
	public Application add(@Valid Application app) throws SchedulerException {
		boolean addNew = (app.getId() == null);
		app = appRepository.save(app);
		if (addNew) {
			// If tasks are empty, that create default tasks
			if (app.getTasks().isEmpty()) {
				Task defaultImporRecordTask = new Task();
				try (InputStream is = defaultImportRecordPayload.getInputStream()) {
					defaultImporRecordTask.setPayload(IOUtils.toString(is));
				} catch (IOException ex) {
					LOG.error("Can't find default import record payload json file", ex);
				}
				Task defaultComputeRecTask = new Task();
				try (InputStream is = defaultComputeRecommendationPayload.getInputStream()) {
					defaultComputeRecTask.setPayload(IOUtils.toString(is));
				} catch (IOException ex) {
					LOG.error("Can't find default compute recommendation payload json file", ex);
				}
				app.addTask(defaultImporRecordTask);
				app.addTask(defaultComputeRecTask);
				app = appRepository.save(app);
			}
			SchedulerOperations schedulerOperations = new SchedulerOperations(scheduler, app);
			schedulerOperations.schedule();
		}
		return app;
	}
	
	public Application addDefault(Tenant tenant) throws SchedulerException {
		Application app = new Application();
		app.setName(tenant.getId() + "_APP");
		List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll();
		if (serviceProviders.isEmpty()) {
			LOG.error("Can't create default app because no service provider is found");
		} else {
			app.setServiceProvider(serviceProviders.get(0));
		}
		app.setTenant(tenant);
		app.setCron("0 50 * * * ?");
		return add(app);
	}
	
	public void delete(Application app) throws SchedulerException {
		if (app != null) {
			SchedulerOperations schedulerOperations = new SchedulerOperations(scheduler, app);
			schedulerOperations.removeSchedule();
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
