package com.predictry.oasis.domain;

/**
 * Status of a service provider.
 * 
 * @author jocki
 *
 */
public enum ServiceProviderStatus {

	RUNNING("Running"), DOWN("Down");
	
	private String message;
	
	private ServiceProviderStatus(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return message;
	}
	
}
