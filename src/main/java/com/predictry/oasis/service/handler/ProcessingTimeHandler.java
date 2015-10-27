package com.predictry.oasis.service.handler;

import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Job;

/**
 * This is a <code>Handler</code> for message that send information about processing time.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class ProcessingTimeHandler extends AbstractStatusHandler {

	@Override
	public String eventName() {
		return null;
	}
	
	@Override
	public String[] requiredFields() {
		return new String[]{"processing_time"};
	}

	@Override
	public void execute(Job job, Map<String, Object> map) {
		Double processingTime = 0.0;
		if (map.get("processing_time") instanceof String) {
			processingTime = Double.parseDouble((String) map.get("processing_time")); 
		} else {
			processingTime = (Double) map.get("processing_time");
		}
		job.setProcessingTime(processingTime);
	}

}
