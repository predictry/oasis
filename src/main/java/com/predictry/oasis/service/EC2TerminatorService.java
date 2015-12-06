package com.predictry.oasis.service;

import java.util.List;

import javax.transaction.Transactional;

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
	
	private static final int MIN_RUNNING_MINUTES = 1 * 60;
	private static final int MAX_RUNNING_MINUTES = 5 * 60;

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
			return s.isRunning() && (s.getLastStarted() != null);
		}).forEach(serviceProvider -> {
			List<Job> jobs = jobRepository.findByApplicationServiceProvider(serviceProvider);
			boolean done = jobs.isEmpty() ? true : jobs.stream().allMatch(j -> 
				(j.getStatus() == JobStatus.FINISH) || (j.getStatus() == JobStatus.FAIL));
			int runningMinutes = Minutes.minutesBetween(serviceProvider.getLastStarted(), LocalDateTime.now()).getMinutes();
			LOG.info("Service provider [" + serviceProvider.getName() + "] is " + (done ? "done" : "not done") + 
					 " and has been running for " + runningMinutes + " minutes (last started is " + 
					serviceProvider.getLastStarted() + " and now is " + LocalDateTime.now() + ")");
			if (((runningMinutes > MIN_RUNNING_MINUTES) && done) || (runningMinutes > MAX_RUNNING_MINUTES)) {
				ec2Service.stopInstance(serviceProvider);
				ec2Service.checkStatus(serviceProvider);
			}
		});
	}
	
}
