package com.predictry.oasis.job;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.service.ApplicationService;

/**
 * Quartz Scheduler's job that invoke REST API (as client).
 * 
 * @author jocki
 *
 */
public class ApplicationJob extends OMSJob {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationJob.class);
	
	@Autowired
	private ApplicationService applicationService;

	@Override
	public void executeInContainer(JobExecutionContext context) {
		Long appId = context.getTrigger().getJobDataMap().getLongFromString("app.id");
		LOG.info("Starting executing application [" + appId + "]");
		try {
			applicationService.execute(appId);
		} catch (Exception e) {
			LOG.error("Error while executing applicatoin [" + appId + "]", e);
		}
	}

}
