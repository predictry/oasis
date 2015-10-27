package com.predictry.oasis.service.handler;

import java.util.Map;

import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Job;

/**
 * This is a <code>Handler</code> for <code>START</code> event.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class StartStatusHandler extends AbstractStatusHandler {

	@Override
	public String eventName() {
		return "START";
	}

	@Override
	public void execute(Job job, Map<String, Object> map) {
		LocalDateTime startTime = LocalDateTime.parse((String) map.get("time"));
		job.setStartTime(startTime);
	}

}
