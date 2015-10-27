package com.predictry.oasis.service.handler;

import java.util.Map;

import com.predictry.oasis.domain.Job;

/**
 * This is an implementation of <code>StatusHandler</code> that provides basic workflow.
 * 
 * @author jocki
 *
 */
public abstract class AbstractStatusHandler implements StatusHandler {

	@Override
	public void handle(Job job, Map<String, Object> map) {
		if ((eventName() != null) && (!eventName().equals(map.get("event")))) {
			return;
		}
		if (requiredFields() != null) {
			for (String field: requiredFields()) {
				if (!map.containsKey(field)) {
					return;
				}
			}
		}
		execute(job, map);
	}
	
	public abstract String eventName();
	
	public String[] requiredFields() {
		return null;
	}
	
	public abstract void execute(Job job, Map<String, Object> map);

}
