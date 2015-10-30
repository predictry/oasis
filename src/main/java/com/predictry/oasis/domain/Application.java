package com.predictry.oasis.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.script.ScriptEngineManager;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class represents the configured task that will be executed 
 * periodically.  Every <code>Application</code> is related to one
 * <code>JobDetail</code> in Quartz Scheduler.
 * 
 * @author jocki
 *
 */
@Entity
public class Application {
	
	private static final Logger LOG = LoggerFactory.getLogger(Application.class);

	@Id @GeneratedValue
	private Long id;
	
	@NotBlank
	private String name;
	
	@ManyToOne
	private Tenant tenant;
	
	@NotBlank
	private String cron;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER) 
	@OrderColumn @JoinColumn(name = "app_id")
	private List<Task> tasks = new ArrayList<>();	
	
	@NotNull @ManyToOne
	private ServiceProvider serviceProvider;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
	public List<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task task) {
		tasks.add(task);
	}
	
	public void editTask(Task task, int index) {
		if ((index >= 0) && (index < tasks.size())) {
			tasks.set(index, task);
		}
	}
	
	public Task getTask(int index) {
		if ((index >= 0) && (index < tasks.size())) {
			return tasks.get(index);
		} else {
			return null;
		}
	}
	
	public void deleteTask(int index) {
		if ((index >= 0) && (index < tasks.size())) {
			tasks.remove(index);
		}
	}
	
	public String getQueueName() {
		Assert.notNull(serviceProvider);
		return String.format("%s.COMMAND", getServiceProvider().getName().toUpperCase());
	}
	
	public Job createJob(ObjectMapper objectMapper, ScriptEngineManager scriptEngineManager) throws JsonParseException, JsonMappingException, IOException {
		if (tasks.isEmpty()) {
			LOG.warn("No task to execute for application [" + getName() + "]");
			return null;
		}
		Task task = tasks.get(0);
		String jobId = String.format("%s_%s_%s", getName(), String.valueOf(tasks.indexOf(task)), LocalDateTime.now().toString("YYYY-MM-dd_HH:mm:ss_SSSS"));
		Job job = task.createJob(objectMapper, scriptEngineManager, jobId, getTenant().getId());
		job.setApplication(this);
		job.setTaskIndex(0);
		return job;
	}
	
	public List<Job> createJobs(ObjectMapper objectMapper, ScriptEngineManager scriptEngineManager) throws JsonParseException, JsonMappingException, IOException {
		if (tasks.isEmpty()) {
			LOG.warn("No task to execute for application [" + getName() + "]");
			return null;
		}
		List<Job> result = new ArrayList<>();
		for (int taskIndex=0; taskIndex < tasks.size(); taskIndex++) {
			Task task = tasks.get(taskIndex);
			String jobId = String.format("%s_%s_%s", getName(), String.valueOf(taskIndex), LocalDateTime.now().toString("YYYY-MM-dd_HH:mm:ss_SSSS"));
			Job job = task.createJob(objectMapper, scriptEngineManager, jobId, getTenant().getId());
			job.setApplication(this);
			job.setTaskIndex(taskIndex);
			result.add(job);
		}
		return result;
	}
}
