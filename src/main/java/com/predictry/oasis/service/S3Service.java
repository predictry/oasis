package com.predictry.oasis.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
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

    /**
     * List the content of bucket by a common prefix.
     *
     * @param bucket is the bucket name.
     * @param prefix is the common prefix.  The result of this method will not contain value of <code>prefix</code>.
     * @return a <code>List</code> that contains keys that match the common prefix in that bucket.
     */
	public List<String> listBucket(String bucket, String prefix) {
        LOG.info("Listing bucket [" + bucket + "] with the following prefix [" + prefix + "]");
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName(bucket);
        request.setPrefix(prefix);
        ObjectListing results = s3Client.listObjects(request);
        return results.getObjectSummaries().stream()
            .filter(s -> !s.getKey().equals(prefix.endsWith("/") ? prefix : prefix + "/"))
            .map(S3ObjectSummary::getKey)
            .collect(Collectors.toList());
	}

    /**
     * Read the content of key in bucket.
     *
     * @param bucket is the bucket name.
     * @param key is the key.
     * @return content of the file in form of <code>String</code>.
     * @throws IOException if there is an error while read file.
     */
    public String read(String bucket, String key) throws IOException {
        LOG.info("Reading file from bucket [" + bucket + "] with the following key [" + key + "]");
		try {
			AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
			GetObjectRequest request = new GetObjectRequest(bucket, key);
			S3Object object = s3Client.getObject(request);
			return IOUtils.toString(object.getObjectContent());
		} catch (AmazonS3Exception ex) {
			LOG.error("Error while reading key [" + key + "]", ex);
			return null;
		}
    }

}
