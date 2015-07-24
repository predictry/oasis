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
	
	public static final String URI_HEARTBEAT = "/heartbeat";
	
	@Id @GeneratedValue
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String baseUrl;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime lastChecked;
	
	private ServiceProviderStatus status;

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

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public LocalDateTime getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(LocalDateTime lastChecked) {
		this.lastChecked = lastChecked;
	}

	public ServiceProviderStatus getStatus() {
		return status;
	}

	public void setStatus(ServiceProviderStatus status) {
		this.status = status;
	}
	
	/**
	 * This method return informational status message.
	 * 
	 * @return a human readable status message.
	 */
	public String getStatusMessage() {
		StringBuilder statusMessage = new StringBuilder();
		if (getStatus() != null) {
			statusMessage.append(getStatus().toString());
			statusMessage.append(" (");
			statusMessage.append(getLastChecked().toString("dd-MM-YYYY HH:MM:ss"));
			statusMessage.append(')');
		} else {
			statusMessage.append("Unknown");
		}
		return statusMessage.toString();
	}

	@Override
	public String toString() {
		return "ServiceProvider [id=" + id + ", name=" + name + ", baseUrl="
				+ baseUrl + ", lastChecked=" + lastChecked + ", status="
				+ status + "]";
	}

}
