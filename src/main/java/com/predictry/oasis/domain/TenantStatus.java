package com.predictry.oasis.domain;

/**
 * This class represents status for a <code>Tenant</code>.
 * 
 * @author jocki
 *
 */
public enum TenantStatus {

	ENABLED("Enabled"), DISABLED("Disabled");
	
	private String description;

	private TenantStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public String toString() {
		return description;
	}
	
}
