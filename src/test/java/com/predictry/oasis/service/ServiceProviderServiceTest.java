package com.predictry.oasis.service;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.repository.ServiceProviderRepository;

public class ServiceProviderServiceTest extends TestCase {
	
	@Autowired
	private EC2Service ec2Service;
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;

	@Test
	public void testCheckStatus() {
		ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.setName("fisher");
		serviceProvider.setInstanceId("i-1b169495");
		serviceProvider.setRegion("ap-southeast-1");
		serviceProvider = serviceProviderRepository.save(serviceProvider);
		
		serviceProvider = ec2Service.checkStatus(serviceProvider);
		assertEquals(true, serviceProvider.isRunning());
		assertNotNull(serviceProvider.getLastChecked());
		
		serviceProvider = new ServiceProvider();
		serviceProvider.setName("unknown instance that seems to be always stopped");
		serviceProvider.setInstanceId("i-64085fa9");
		serviceProvider.setRegion("ap-southeast-1");
		serviceProvider = serviceProviderRepository.save(serviceProvider);
		
		serviceProvider = ec2Service.checkStatus(serviceProvider);
		assertEquals(false, serviceProvider.isRunning());
		assertNotNull(serviceProvider.getLastChecked());
	}
	
}
