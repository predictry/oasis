package com.predictry.oasis.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;

/**
 * A specific tenant and all information about it.
 * 
 * @author jocki
 *
 */
@Entity
public class Tenant {

	@Id
	private String id;

	@NotBlank
	private String name;
	
	@NotNull
	private TenantStatus status = TenantStatus.ENABLED;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime registeredDate;
	
	public Tenant(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Tenant(String id, String name, LocalDateTime registeredDate) {
		this.id = id;
		this.name = name;
		this.registeredDate = registeredDate;
	}
	
	public Tenant() { }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TenantStatus getStatus() {
		return status;
	}

	public void setStatus(TenantStatus status) {
		this.status = status;
	}

	public LocalDateTime getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(LocalDateTime registeredDate) {
		this.registeredDate = registeredDate;
	}
	
}
