package com.predictry.oasis.service;

import java.util.List;

import javax.transaction.Transactional;

import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.JobStatus;
import com.predictry.oasis.repository.JobRepository;
import com.predictry.oasis.repository.ServiceProviderRepository;

/**
 * This service provide operations to stop EC2 instances that are not being used.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class EC2TerminatorService {
	
	private static final int MIN_RUNNING_MINUTE = 5;
	private static final int MAX_RUNNING_HOUR = 5;

	private static final Logger LOG = LoggerFactory.getLogger(EC2TerminatorService.class);
	
	@Autowired
	private EC2Service ec2Service;
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	public void terminateInstances() {
		LOG.info("Checking for instances to be stopped...");
		serviceProviderRepository.findAll().stream().filter(s -> {
			ec2Service.checkStatus(s);
			return s.isRunning();
		}).forEach(serviceProvider -> {
			List<Job> jobs = jobRepository.findByApplicationServiceProvider(serviceProvider);
			boolean done = jobs.isEmpty() ? true : jobs.stream().anyMatch(j -> j.getStatus() != JobStatus.STARTED);
			int runningHours = Hours.hoursBetween(serviceProvider.getLastStarted(), LocalDateTime.now()).getHours();
			int runningMinutes = Minutes.minutesBetween(serviceProvider.getLastStarted(), LocalDateTime.now()).getMinutes();
			if (((runningMinutes > MIN_RUNNING_MINUTE) && done) || (runningHours > MAX_RUNNING_HOUR)) {
				ec2Service.stopInstance(serviceProvider);
				ec2Service.checkStatus(serviceProvider);
			}
		});
	}
	
}
