package com.predictry.oasis.job;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.service.MetricService;

/**
 * Quartz Scheduler's job that measure the value for internal metrics.
 * 
 * @author jocki
 *
 */
public class MetricJob extends OMSJob {

	@Autowired
	private MetricService metricService;
	
	@Override
	public void executeInContainer(JobExecutionContext context) {
		metricService.measureStorage();
		metricService.measureSpeed();
	}

}
