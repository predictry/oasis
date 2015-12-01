package com.predictry.oasis.service;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.repository.ServiceProviderRepository;

/**
 * This class provides AWS EC2 services for <code>ServiceProvider</code>.
 * 
 * @author jocki
 *
 */
@Service @Profile("!test")
@Transactional
public class EC2ServiceImpl implements EC2Service {
	
	private static final int EC2_RUNNING_STATUS_CODE = 16;

	private static final Logger LOG = LoggerFactory.getLogger(EC2ServiceImpl.class);
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	/**
	 * Check if EC2 instance for this service provider is running and update the status
	 * of the service provider.
	 * 
	 * @param serviceProvider the service provider to check for.
	 */
	public ServiceProvider checkStatus(ServiceProvider serviceProvider) {
		serviceProvider = serviceProviderRepository.findOne(serviceProvider.getId());
		AmazonEC2Client ec2Client = new AmazonEC2Client(new ProfileCredentialsProvider("fisher"));
		ec2Client.setRegion(Region.getRegion(Regions.fromName(serviceProvider.getRegion())));
		DescribeInstanceStatusRequest statusRequest = new DescribeInstanceStatusRequest();
		statusRequest.withInstanceIds(serviceProvider.getInstanceId());
		DescribeInstanceStatusResult result = ec2Client.describeInstanceStatus(statusRequest);
		if (!result.getInstanceStatuses().isEmpty()) {
			serviceProvider.setRunning(result.getInstanceStatuses().get(0).getInstanceState().getCode() == EC2_RUNNING_STATUS_CODE);
			LOG.info("Running status for " + serviceProvider.getName() + " is " + serviceProvider.isRunning());
		} else {
			serviceProvider.setRunning(false);
		}
		serviceProvider.setLastChecked(LocalDateTime.now());
		return serviceProvider;
	}
	
	/**
	 * Start EC2 instance associated with <code>ServiceProvider</code>.
	 * 
	 * @param serviceProvider is the <code>ServiceProvider</code> to start.
	 */
	public void startInstance(ServiceProvider serviceProvider) {
		serviceProvider = serviceProviderRepository.findOne(serviceProvider.getId());
		AmazonEC2Client ec2Client = new AmazonEC2Client(new ProfileCredentialsProvider("fisher"));
		ec2Client.setRegion(Region.getRegion(Regions.fromName(serviceProvider.getRegion())));
		StartInstancesRequest startInstanceRequest = new StartInstancesRequest();
		startInstanceRequest.withInstanceIds(serviceProvider.getInstanceId());
		LOG.info("Starting instance " + serviceProvider);
		ec2Client.startInstances(startInstanceRequest);
		serviceProvider.setLastStarted(LocalDateTime.now());
	}
	
	/**
	 * Stop EC2 instance associated with <code>ServiceProvider</code>.
	 * 
	 * @param serviceProvider is the <code>ServiceProvider</code> to stop.
	 */
	public void stopInstance(ServiceProvider serviceProvider) {
		AmazonEC2Client ec2Client = new AmazonEC2Client(new ProfileCredentialsProvider("fisher"));
		ec2Client.setRegion(Region.getRegion(Regions.fromName(serviceProvider.getRegion())));
		StopInstancesRequest stopInstanceRequest = new StopInstancesRequest();
		stopInstanceRequest.withInstanceIds(serviceProvider.getInstanceId());
		LOG.info("Stopping instance " + serviceProvider);
		ec2Client.stopInstances(stopInstanceRequest);
	}
	
}
