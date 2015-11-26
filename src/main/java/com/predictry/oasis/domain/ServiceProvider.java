package com.predictry.oasis.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;

/**
 * Entity that represent individual ServiceProvider.
 * 
 * @author jocki
 *
 */
@Entity
public class ServiceProvider {
	
	@Id @GeneratedValue
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String instanceId;
	
	@NotBlank
	private String region;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime lastChecked;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime lastStarted;
	
	private Boolean running = Boolean.FALSE;

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

	public LocalDateTime getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(LocalDateTime lastChecked) {
		this.lastChecked = lastChecked;
	}
	
	public LocalDateTime getLastStarted() {
		return lastStarted;
	}

	public void setLastStarted(LocalDateTime lastStarted) {
		this.lastStarted = lastStarted;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public boolean isRunning() {
		return (running == null)? false: running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * This method return informational status message.
	 * 
	 * @return a human readable status message.
	 */
	public String getStatusMessage() {
		StringBuilder statusMessage = new StringBuilder();
		statusMessage.append(isRunning()? "Running": "Stopped");
		if (getLastChecked() != null) {
			statusMessage.append(" (");
			statusMessage.append(getLastChecked().toString("dd-MM-YYYY HH:MM:ss"));
			statusMessage.append(')');
		}
		return statusMessage.toString();
	}

	@Override
	public String toString() {
		return "ServiceProvider [id=" + id + ", name=" + name + ", instanceId=" + instanceId + ", region=" + region
				+ ", lastChecked=" + lastChecked + ", running=" + running + "]";
	}

}
