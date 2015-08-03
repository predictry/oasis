package com.predictry.oasis.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Basic class for OMS scheduled jobs.
 * 
 * @author jocki
 *
 */
public abstract class OMSJob extends QuartzJobBean {

	@Override
	protected final void executeInternal(JobExecutionContext context) throws JobExecutionException {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		executeInContainer(context);
		
	}
	
	public abstract void executeInContainer(JobExecutionContext context);

}
