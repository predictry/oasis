package com.predictry.oasis.domain;

import org.junit.Test;
import static org.junit.Assert.*;

public class JobTest {
	
	@Test
	public void testRepeat() {
		Job job = new Job();
		assertEquals(0, job.getNumOfRepeat().intValue());
		
		for (int i=0; i<20; i++) {
			boolean r = job.retry();
			assertTrue(r);
			assertEquals(i+1, job.getNumOfRepeat().intValue());
			assertEquals(JobStatus.REPEAT, job.getStatus());
			assertNotNull(job.getLastRepeat());
		}
		
		boolean r = job.retry();
		assertFalse(r);
		assertEquals(20, job.getNumOfRepeat().intValue());
		assertEquals(JobStatus.FAIL, job.getStatus());
	}

}
