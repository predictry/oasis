package com.predictry.oasis.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

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

	@Test
	public void testListBucket() {
		List<String> results = s3Service.listBucket("predictry", "data/tenants/latihan/history/2016/02/15");
		assertEquals(4, results.size());
		assertTrue(results.contains("data/tenants/latihan/history/2016/02/15/user1.json"));
		assertTrue(results.contains("data/tenants/latihan/history/2016/02/15/user2.json"));
		assertTrue(results.contains("data/tenants/latihan/history/2016/02/15/user3.json"));
		assertTrue(results.contains("data/tenants/latihan/history/2016/02/15/user4.json"));

		results = s3Service.listBucket("predictry", "data/tenants/latihan/history/2015/02/15");
		assertTrue(results.isEmpty());
	}

	@Test
	public void testRead() throws IOException {
		String user1 = s3Service.read("predictry", "data/tenants/latihan/history/2016/02/15/user1.json");
		assertEquals("{\n" +
			"\t\"email\": \"user1@gmail.com\",\n" +
			"\t\"buys\": [\"item1\"],\n" +
			"\t\"views\": [\"item1\", \"item2\", \"item3\", \"item4\"]\n" +
			"}\n", user1);
	}

    @Test(expected = AmazonS3Exception.class)
    public void testReadNonExisting() throws IOException {
        s3Service.read("predictry", "data/tenants/latihan/history/2015/02/15/user1.json");
    }

}
