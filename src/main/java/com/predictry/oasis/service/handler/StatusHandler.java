package com.predictry.oasis.service.handler;

import java.util.Map;

import com.predictry.oasis.domain.Job;

/**
 * <code>Handler</code> is used to handle incoming communication (such as reply) to OMS.
 *  
 * @author jocki
 *
 */
public interface StatusHandler {

	public void handle(Job job, Map<String, Object> map);
	
}
