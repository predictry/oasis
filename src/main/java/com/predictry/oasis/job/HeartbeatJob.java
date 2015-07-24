package com.predictry.oasis.job;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.service.HeartbeatService;

public class HeartbeatJob extends OMSJob {

	@Autowired
	private HeartbeatService heartbeatService;
	
	@Override
	public void executeInContainer(JobExecutionContext context) {
		heartbeatService.heartbeat();
	}

}
