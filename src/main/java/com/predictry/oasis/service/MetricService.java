package com.predictry.oasis.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.oasis.domain.metric.SpeedMetric;
import com.predictry.oasis.domain.metric.StorageMetric;
import com.predictry.oasis.util.JsonMessageCreator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This class provides service related to metrics.
 * 
 * @author jocki
 *
 */
@Service
public class MetricService {

	private static final Logger LOG = LoggerFactory.getLogger(MetricService.class);
	
	
	private static final String NEO4J_DATA_DIR = "/volumes/neo/neo4j/data";
	private static final String TEST_REQUEST_URL = "https://s3-ap-southeast-1.amazonaws.com/predictry/data/tenants/latihan/recommendations/similiar/xxx.json";
	
	@Autowired @Qualifier("topic")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private S3Service s3Service;
	
	/**
	 * Calculate the total storage of data stored in harddisk (Neo4j) and S3.
	 * This method will send the result to measurement topic.
	 * 
	 */
	@SuppressFBWarnings("DMI_HARDCODED_ABSOLUTE_FILENAME")
	public void measureStorage() {
		long totalNeo4j = 0L, totalS3Trackings = 0L;
		
		// Calculate Neo4J storage size
		File neo4jDataDir = new File(NEO4J_DATA_DIR);
		if (neo4jDataDir.exists()) {
			totalNeo4j = FileUtils.sizeOfDirectory(neo4jDataDir);
		}
		LOG.info("neo4j total storage is " + totalNeo4j);
		
		// Calculate S3 'trackings' bucket
		totalS3Trackings = s3Service.bucketSize("trackings");
		
		StorageMetric storageMetric = new StorageMetric();
		storageMetric.setTime(LocalDateTime.now());
		if (totalNeo4j > 0) {
			storageMetric.addStorage("neo4j", (double) totalNeo4j);
		}
		if (totalS3Trackings > 0) {
			storageMetric.addStorage("s3", (double) totalS3Trackings);
		}
		jmsTemplate.send("ADMIN.METRIC", new JsonMessageCreator(objectMapper, storageMetric));
	}
	
	/**
	 * Calculate the average speed to retrieve recommendation from CloudFront.
	 * This method will send the result to measurement topic.
	 */
	public void measureSpeed() {
		HttpClient client = HttpClientBuilder.create().build();
		long startTime = System.currentTimeMillis();
		try {
			client.execute(new HttpGet(TEST_REQUEST_URL));
		} catch (IOException e) {
			LOG.error("Exception while requesting recommendation to S3", e);
			return;
		}
		long elapsed = System.currentTimeMillis() - startTime;
		LOG.info("Recommendation speed is " + elapsed);
		
		SpeedMetric speedMetric = new SpeedMetric();
		speedMetric.setTime(LocalDateTime.now());
		speedMetric.setSpeed(elapsed);
		jmsTemplate.send("ADMIN.METRIC", new JsonMessageCreator(objectMapper, speedMetric));
	}
	
}
