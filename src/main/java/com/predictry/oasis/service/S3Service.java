package com.predictry.oasis.service;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;

/**
 * This services provides gateway to S3 operations.
 * 
 * @author jocki
 *
 */
@Service
public class S3Service {

	private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
	
	private static final int DAY_PERIOD = 24 * 60 * 60;
	
	/**
	 * This method will calculate the size of a S3 bucket based on statistics gathered by CloudWatch.
	 * 
	 * @param bucket the bucket name to search for.
	 * @return size of the bucket in bytes.
	 */
	public long bucketSize(String bucket) {
		AmazonCloudWatchClient cloudWatchClient = new AmazonCloudWatchClient(new ProfileCredentialsProvider("fisher"));
		cloudWatchClient.configureRegion(Regions.AP_SOUTHEAST_1);
		Date startTime = LocalDate.now().minusDays(1).toDate();
		Date endTime = LocalDate.now().toDate();
		GetMetricStatisticsRequest metricStatisticRequests = new GetMetricStatisticsRequest()
			.withStartTime(startTime).withEndTime(endTime)
			.withNamespace("AWS/S3")
			.withPeriod(DAY_PERIOD)
			.withMetricName("BucketSizeBytes")
			.withDimensions(
				new Dimension().withName("BucketName").withValue(bucket),
				new Dimension().withName("StorageType").withValue("StandardStorage"))
			.withStatistics("Maximum");
		GetMetricStatisticsResult result = cloudWatchClient.getMetricStatistics(metricStatisticRequests);
		List<Datapoint> datapoints = result.getDatapoints();
		datapoints.sort((Datapoint d1, Datapoint d2) -> d1.getTimestamp().compareTo(d2.getTimestamp()));
		double size = 0.0;
		if (!datapoints.isEmpty()) {
			size = datapoints.get(datapoints.size() - 1).getMaximum();
		}
		LOG.info("Size for bucket [" + bucket + "] is: " + size);
		return (long) size;
	}
	
}
