package com.predictry.oasis.domain;

import org.junit.Test;
import static org.junit.Assert.*;

import org.joda.time.LocalDateTime;

public class JobTest {

	@Test
	public void testFinish() {
		Job job = new Job("job1", LocalDateTime.now(), "test payload");
		LocalDateTime finishTime = LocalDateTime.now();
		job.finish(finishTime);
		assertEquals(JobStatus.FINISH, job.getStatus());
	}
	
	@Test
	public void testFail() {
		Job job = new Job("job1", LocalDateTime.now(), "test payload");
		LocalDateTime failTime = LocalDateTime.now();
		String reason = "fail reason";
		job.fail(failTime, reason);
		assertEquals(JobStatus.FAIL, job.getStatus());
		assertEquals(reason, job.getReason());
	}
	
}
