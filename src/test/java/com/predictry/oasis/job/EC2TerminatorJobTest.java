package com.predictry.oasis.job;

import static org.quartz.impl.matchers.GroupMatcher.anyTriggerGroup;

import java.util.ArrayList;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.JobStatus;
import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.repository.JobRepository;
import com.predictry.oasis.repository.ServiceProviderRepository;
import com.predictry.oasis.service.ApplicationService;
import com.predictry.oasis.service.EC2TerminatorService;
import com.predictry.oasis.service.TestCase;

import static org.junit.Assert.*;

public class EC2TerminatorJobTest extends TestCase {

	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private EC2TerminatorService ec2TerminatorService;
	
	private ServiceProvider sp;
	
	private Application app;
	
	@Before
	public void before() throws SchedulerException {
		// Delete all schedules
		scheduler.unscheduleJobs(new ArrayList<>(scheduler.getTriggerKeys(anyTriggerGroup())));
		
		// Create new service provider
		sp = new ServiceProvider();
		sp.setName("sp1");
		sp.setInstanceId("i1234");
		sp.setRegion("ap-southeast-1");
		sp.setRunning(true);
		sp.setLastStarted(LocalDateTime.now().minusHours(3));
		sp = serviceProviderRepository.save(sp);
		
		// Create new app
		app = new Application();
		app.setName("test application");
		app.setCron("0 11 11 11 11 ?");
		app.setServiceProvider(sp);
		Task task = new Task();
		task.setPayload("{\"test\": true}");
		app.addTask(task);
		applicationService.add(app);
	}
	
	@Test
	public void testTerminateSP_case1() {
		// Create finished job
		Job job1 = new Job("job1", LocalDateTime.now(), "{\"test\":true}");
		job1.setApplication(app);
		job1.setStatus(JobStatus.FINISH);
		jobRepository.save(job1);
		
		Job job2 = new Job("job2", LocalDateTime.now(), "{\"test\":true}");
		job2.setApplication(app);
		job2.setStatus(JobStatus.FINISH);
		jobRepository.save(job2);
		
		// Launch job
		ec2TerminatorService.terminateInstances();
		
		// Make sure the instance is stopped
		assertEquals(false, sp.isRunning());
	}
	
	@Test
	public void testTerminateSP_case2() {
		// Create finished job
		Job job1 = new Job("job1", LocalDateTime.now(), "{\"test\":true}");
		job1.setApplication(app);
		job1.setStatus(JobStatus.FAIL);
		jobRepository.save(job1);
		
		Job job2 = new Job("job2", LocalDateTime.now(), "{\"test\":true}");
		job2.setApplication(app);
		job2.setStatus(JobStatus.FINISH);
		jobRepository.save(job2);
		
		// Launch job
		ec2TerminatorService.terminateInstances();
		
		// Make sure the instance is stopped
		assertEquals(false, sp.isRunning());
	}
	
	@Test
	public void testTerminateLongRunningSP() {
		// Create long running service provider
		sp.setLastStarted(LocalDateTime.now().minusHours(7));
		sp.setRunning(true);
		serviceProviderRepository.save(sp);
		
		// Launch job
		ec2TerminatorService.terminateInstances();
		
		// Make sure the instance is stopped
		assertEquals(false, sp.isRunning());
	}
	
	@Test
	public void testDontTerminateSP_case1() {
		// Create unfinished job
		Job job1 = new Job("job1", LocalDateTime.now(), "{\"test\":true}");
		job1.setApplication(app);
		job1.setStatus(JobStatus.STARTED);
		jobRepository.save(job1);
		
		Job job2 = new Job("job2", LocalDateTime.now(), "{\"test\":true}");
		job2.setApplication(app);
		job2.setStatus(JobStatus.FINISH);
		jobRepository.save(job2);

		// Launch job
		ec2TerminatorService.terminateInstances();
		
		// Make sure the instance is not stopped
		assertEquals(true, sp.isRunning());
	}
	
	@Test
	public void testDontTerminateSP_case2() {
		// Create unfinished job
		Job job1 = new Job("job1", LocalDateTime.now(), "{\"test\":true}");
		job1.setApplication(app);
		job1.setStatus(JobStatus.REPEAT);
		jobRepository.save(job1);
		
		Job job2 = new Job("job2", LocalDateTime.now(), "{\"test\":true}");
		job2.setApplication(app);
		job2.setStatus(JobStatus.FINISH);
		jobRepository.save(job2);

		// Launch job
		ec2TerminatorService.terminateInstances();
		
		// Make sure the instance is not stopped
		assertEquals(true, sp.isRunning());
	}
	
	@Test
	public void testDontTerminateSPLessThanOneHour() {
		// Create unfinished job
		sp.setLastStarted(LocalDateTime.now().minusMinutes(30));
		sp.setRunning(true);
		serviceProviderRepository.save(sp);
		
		// Launch job
		ec2TerminatorService.terminateInstances();
				
		// Make sure the instance is not stopped
		assertEquals(true, sp.isRunning());
	}
}
