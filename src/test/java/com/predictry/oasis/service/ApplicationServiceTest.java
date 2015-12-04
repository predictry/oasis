
package com.predictry.oasis.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.quartz.impl.matchers.GroupMatcher.anyTriggerGroup;
import static org.quartz.impl.matchers.GroupMatcher.groupEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.config.SchedulerConfig;
import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.job.ApplicationJob;
import com.predictry.oasis.repository.ServiceProviderRepository;
import com.predictry.oasis.repository.TenantRepository;

public class ApplicationServiceTest extends TestCase {
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private TenantRepository tenantRepository;
	
	@Autowired
	private Scheduler scheduler;
	
	private Application app;
	
	@Before
	public void before() throws SchedulerException {
		// Delete all schedules
		scheduler.unscheduleJobs(new ArrayList<>(scheduler.getTriggerKeys(anyTriggerGroup())));
		
		// Create new service provider
		ServiceProvider sp = new ServiceProvider();
		sp.setName("sp1");
		sp.setInstanceId("i1234");
		sp.setRegion("ap-southeast-1");
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
	public void testAdd() throws SchedulerException {
		// Check if application is created
		List<Application> apps = applicationService.findAll();
		app = apps.stream().filter(a ->  a.getName().equals("test application")).findFirst().get();
		assertNotNull(app.getId());
		assertEquals("test application", app.getName());
		assertNull(app.getTenant());
		assertEquals("sp1", app.getServiceProvider().getName());
		assertEquals(1, app.getTasks().size());
		
		// Check if it is scheduled
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
			SchedulerConfig.QUARTZ_APP_GROUP));
		TriggerKey triggerKey = triggerKeys.stream().filter(t -> t.getName().equals("test application")).findFirst().get();
		assertEquals("Only one schedule should be created!", 1, triggerKeys.stream().filter(t -> t.getName().equals("test application")).count());
		assertNotNull(triggerKey);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		assertNotNull(trigger.getNextFireTime());
		JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
		assertEquals(ApplicationJob.class, jobDetail.getJobClass());
	}
	
	@Test
	public void testAddDefault() throws SchedulerException {
		Tenant tenant = new Tenant("tenant1", "tenant1");
		tenant = tenantRepository.save(tenant);
		Application app = applicationService.addDefault(tenant);
		
		// Check if application is created
		assertEquals("tenant1_APP", app.getName());
		assertNotNull("0 50 * * * ?", app.getCron());
		assertEquals(tenant, app.getTenant());
		assertEquals("sp1", app.getServiceProvider().getName());
		assertEquals(2, app.getTasks().size());
		assertNotNull(app.getTask(0).getPayload());
		assertNotNull(app.getTask(1).getPayload());
		
		// Check if it is scheduled
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
				SchedulerConfig.QUARTZ_APP_GROUP));
		TriggerKey triggerKey = triggerKeys.stream().filter(t -> t.getName().equals("tenant1_APP")).findFirst().get();
		assertEquals("Only one schedule should be created!", 1, triggerKeys.stream().filter(t -> t.getName().equals("tenant1_APP")).count());
		assertNotNull(triggerKey);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		assertNotNull(trigger.getNextFireTime());
		JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
		assertEquals(ApplicationJob.class, jobDetail.getJobClass());
	}
	
	@Test
	public void testUpdate() throws SchedulerException {
		Task task = new Task();
		task.setPayload("{\"test\": true}");
		app.addTask(task);
		applicationService.update(app);
		
		// Check if new task is added
		List<Application> apps = applicationService.findAll();
		app = apps.stream().filter(a ->  a.getName().equals("test application")).findFirst().get();
		assertEquals(2, app.getTasks().size());
		
		// Make sure it is still scheduled
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
				SchedulerConfig.QUARTZ_APP_GROUP));
		TriggerKey triggerKey = triggerKeys.stream().filter(t -> t.getName().equals("test application")).findFirst().get();
		assertEquals("Only one schedule should be created!", 1, triggerKeys.stream().filter(t -> t.getName().equals("test application")).count());
		assertNotNull(triggerKey);
		Trigger trigger = scheduler.getTrigger(triggerKey);
		assertNotNull(trigger.getNextFireTime());
		JobDetail jobDetail = scheduler.getJobDetail(trigger.getJobKey());
		assertEquals(ApplicationJob.class, jobDetail.getJobClass());
	}
	
	@Test
	public void testDelete() throws SchedulerException {
		applicationService.delete(app);
		
		// Make sure it is not in database anymore
		List<Application> apps = applicationService.findAll();
		assertEquals(0, apps.stream().filter(a -> a.getName().equals("test application")).count());
		
		// Make sure it is not in the scheduler
		Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(groupEquals(
			SchedulerConfig.QUARTZ_APP_GROUP));
		assertEquals(0, triggerKeys.stream().filter(t -> t.getName().equals("test application")).count());
	}
	
	
}
