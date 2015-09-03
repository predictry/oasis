package com.predictry.oasis.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.NotBlank;

/**
 * This class represent a single task that is part of an <code>Application</code>.
 * 
 * @author jocki
 *
 */
@Embeddable
public class Task {

	@NotBlank @Column(length = 2000)
	private String payload;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
}
