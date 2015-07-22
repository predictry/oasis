package com.predictry.oasis.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotBlank;

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
	private String baseUrl;

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

}
