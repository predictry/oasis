package com.predictry.oasis.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;
import org.springframework.util.Assert;

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

	@Id @GeneratedValue
	private Long id;
	
	@NotBlank
	private String name;
	
	@ManyToOne
	private Tenant tenant;
	
	@NotBlank
	private String cron;
	
	@ElementCollection(fetch = FetchType.EAGER) @CollectionTable @OrderColumn
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
	
	public String generateJobId(Task task) {
		int indexNo = tasks.indexOf(task);
		return String.format("%s_%s_%s", getName(), String.valueOf(indexNo), LocalDateTime.now().toString("YYYY-MM-dd_HH:mm"));
	}
	
}
