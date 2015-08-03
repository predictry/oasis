package com.predictry.oasis.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represents the configured task that will be executed 
 * periodically.  Every <code>Task</code> is related to one
 * <code>JobDetail</code> in Quartz Scheduler.
 * 
 * @author jocki
 *
 */
@Entity
public class Task {

	@Id @GeneratedValue
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String cron;
	
	@NotBlank
	private String payload;
	
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

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
}
