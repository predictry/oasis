package com.predictry.oasis.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.ServiceProvider;

@Service @Profile("test")
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
