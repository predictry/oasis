package com.predictry.oasis.job;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz Scheduler's job that invoke REST API (as client).
 * 
 * @author stewart
 *
 */
public class ApplicationJob extends OMSJob {

	private static final Logger LOG = LoggerFactory.getLogger(ApplicationJob.class);

	@Override
	public void executeInContainer(JobExecutionContext context) {
		Long appId = context.getJobDetail().getJobDataMap().getLong("app.id");
		LOG.info("Starting executing application [" + appId + "]");
	}

}
