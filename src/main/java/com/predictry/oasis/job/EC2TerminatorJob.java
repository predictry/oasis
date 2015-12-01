package com.predictry.oasis.job;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.service.EC2TerminatorService;

/**
 * This is a scheduled job that will regularly check for EC2 instances that represents
 * service provider and stopped them when they're not being used.
 * 
 * @author jocki
 *
 */
public class EC2TerminatorJob extends OMSJob {

	@Autowired
	private EC2TerminatorService ec2TerminatorService;
	
	@Override
	public void executeInContainer(JobExecutionContext context) {
		ec2TerminatorService.terminateInstances();
	}

}
