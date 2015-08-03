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
public class InvokeRestJob extends OMSJob {

	private static final Logger LOG = LoggerFactory.getLogger(InvokeRestJob.class);

	@Override
	public void executeInContainer(JobExecutionContext context) {
		LOG.info("Starting executing service provider job...");
	}

}
