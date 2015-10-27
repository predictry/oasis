package com.predictry.oasis.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.JobStatus;
import com.predictry.oasis.service.handler.FailureStatusHandler;
import com.predictry.oasis.service.handler.ProcessingTimeHandler;
import com.predictry.oasis.service.handler.StartStatusHandler;
import com.predictry.oasis.service.handler.SuccessStatusHandler;

public class HandlerTest {
	
	@Test
	public void testStartStatusHandler() {
		StartStatusHandler startStatusHandler = new StartStatusHandler();		
		Job job = new Job();
		Map<String, Object> message = new HashMap<>();
		message.put("serviceProvider", "SP1");
		message.put("jobId", "job1");
		message.put("event", "START");
		message.put("time", "2015-12-01T09:30:00");
		startStatusHandler.handle(job, message);
		assertEquals(JobStatus.STARTED, job.getStatus());
		assertEquals(2015, job.getStartTime().getYear());
		assertEquals(12, job.getStartTime().getMonthOfYear());
		assertEquals(1, job.getStartTime().getDayOfMonth());
		assertEquals(9, job.getStartTime().getHourOfDay());
		assertEquals(30, job.getStartTime().getMinuteOfHour());
		assertEquals(0, job.getStartTime().getSecondOfMinute());
	}
	
	@Test
	public void testSuccessStatusHandler() {
		SuccessStatusHandler sucessStatusHandler = new SuccessStatusHandler();		
		Job job = new Job();
		Map<String, Object> message = new HashMap<>();
		message.put("serviceProvider", "SP1");
		message.put("jobId", "job1");
		message.put("event", "SUCCESS");
		message.put("time", "2015-12-01T09:30:00");
		sucessStatusHandler.handle(job, message);
		assertEquals(JobStatus.FINISH, job.getStatus());
		assertEquals(2015, job.getEndTime().getYear());
		assertEquals(12, job.getEndTime().getMonthOfYear());
		assertEquals(1, job.getEndTime().getDayOfMonth());
		assertEquals(9, job.getEndTime().getHourOfDay());
		assertEquals(30, job.getEndTime().getMinuteOfHour());
		assertEquals(0, job.getEndTime().getSecondOfMinute());
	}

	@Test
	public void testFailureStatusHandler() {
		FailureStatusHandler failureStatusHandler = new FailureStatusHandler();		
		Job job = new Job();
		Map<String, Object> message = new HashMap<>();
		message.put("serviceProvider", "SP1");
		message.put("jobId", "job1");
		message.put("event", "FAILURE");
		message.put("reason", "Can't connect to database");
		message.put("time", "2015-12-01T09:30:00");
		failureStatusHandler.handle(job, message);
		assertEquals(JobStatus.FAIL, job.getStatus());
		assertEquals("Can't connect to database", job.getReason());
		assertEquals(2015, job.getEndTime().getYear());
		assertEquals(12, job.getEndTime().getMonthOfYear());
		assertEquals(1, job.getEndTime().getDayOfMonth());
		assertEquals(9, job.getEndTime().getHourOfDay());
		assertEquals(30, job.getEndTime().getMinuteOfHour());
		assertEquals(0, job.getEndTime().getSecondOfMinute());
	}
	
	@Test
	public void testProcessingTimeHandler() {
		ProcessingTimeHandler processingTimeHandler = new ProcessingTimeHandler();		
		Job job = new Job();
		Map<String, Object> message1 = new HashMap<>();
		message1.put("serviceProvider", "SP1");
		message1.put("jobId", "job1");
		message1.put("processing_time", 60500.0);
		processingTimeHandler.handle(job, message1);
		assertEquals(60500, job.getProcessingTime().longValue());
		
		Map<String, Object> message2 = new HashMap<>();
		message2.put("serviceProvider", "SP1");
		message2.put("jobId", "job1");
		message2.put("processing_time", "54321");
		processingTimeHandler.handle(job, message2);
		assertEquals(54321l, job.getProcessingTime().longValue());
	}
}
