package com.predictry.oasis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;

/**
 * This services provides gateway to S3 operations.
 * 
 * @author jocki
 *
 */
@Service
public class S3Service {

	// TODO: Improve this to get metrics from S3 later!
	
	private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
	
	public long size(String bucket, String key) {
		AmazonS3Client s3Client = new AmazonS3Client(new ProfileCredentialsProvider("fisher"));
		GetObjectMetadataRequest request = new GetObjectMetadataRequest(bucket, key);
		long size = s3Client.getObjectMetadata(request).getInstanceLength();
		LOG.info("Size of (bucket=" + bucket + ", key=" + key + ") is " + size);
		return size;
	}
	
}
