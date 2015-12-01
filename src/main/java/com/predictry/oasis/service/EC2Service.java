package com.predictry.oasis.service;

import com.predictry.oasis.domain.ServiceProvider;

/**
 * This service provides operation related to EC2 such as starting and stopping an instance.
 * 
 * @author jocki
 *
 */
public interface EC2Service {

	public ServiceProvider checkStatus(ServiceProvider serviceProvider);
	
	public void startInstance(ServiceProvider serviceProvider);
	
	public void stopInstance(ServiceProvider serviceProvider);
	
}
