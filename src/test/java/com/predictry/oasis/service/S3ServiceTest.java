package com.predictry.oasis.service;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class S3ServiceTest extends TestCase {

	private static final Logger LOG = LoggerFactory.getLogger(S3ServiceTest.class);
	
	@Autowired
	private S3Service s3Service;
	
	@Test
	public void testSize() {
		long total = s3Service.bucketSize("trackings");
		LOG.info("Total size for bucket 'trackings' is " + total);
		assertTrue(total > 0);
	}
	
}
