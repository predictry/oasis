package com.predictry.oasis.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.ServiceProvider;

/**
 * This is a stub class for <code>EC2Service</code> that does nothing.
 * It is useful to run the application without affecting any EC2 instances.
 * 
 * @author jocki
 *
 */
@Service @Profile("!prod")
public class EC2ServiceStub implements EC2Service {

	@Override
	public ServiceProvider checkStatus(ServiceProvider serviceProvider) {
		return serviceProvider;
	}

	@Override
	public void startInstance(ServiceProvider serviceProvider) {
		serviceProvider.setRunning(true);
	}

	@Override
	public void stopInstance(ServiceProvider serviceProvider) {
		serviceProvider.setRunning(false);
	}

}
