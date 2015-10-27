package com.predictry.oasis.service.handler;

import java.util.Map;

import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.JobStatus;

/**
 * This is a <code>Handler</code> for <code>FAILURE</code> event.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class FailureStatusHandler extends AbstractStatusHandler {

	@Override
	public String eventName() {
		return "FAILURE";
	}
	
	@Override
	public String[] requiredFields() {
		return new String[] {"reason", "time"};
	}

	@Override
	public void execute(Job job, Map<String, Object> map) {
		job.setStatus(JobStatus.FAIL);
		job.setEndTime(LocalDateTime.parse((String) map.get("time")));
		job.setReason((String) map.get("reason"));
	}

}
