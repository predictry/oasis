package com.predictry.oasis.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotBlank;

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
	
}
