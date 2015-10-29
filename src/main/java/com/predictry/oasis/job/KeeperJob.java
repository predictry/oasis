package com.predictry.oasis.job;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.config.SchedulerConfig;
import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.JobStatus;
import com.predictry.oasis.repository.JobRepository;
import com.predictry.oasis.service.ExecutorService;

/**
 * This job will retry pending or failed jobs.
 * 
 * @author jocki
 *
 */
@Transactional
public class KeeperJob extends OMSJob {
	
	private static final Logger LOG = LoggerFactory.getLogger(KeeperJob.class);
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private ExecutorService executorService;

	@Override
	public void executeInContainer(JobExecutionContext context) {
		LOG.info("Executing keeper job.");
		List<JobStatus> runningStatuses = Arrays.stream(JobStatus.values()).filter(s -> s.isRunning()).collect(Collectors.toList());
		List<Job> runningJobs = jobRepository.findByStatusIn(runningStatuses);
		LOG.info("Keeper found " + runningJobs.size() + " running jobs.");
		for (Job job: runningJobs) {
			LocalDateTime lastTime = (job.getStatus() == JobStatus.STARTED) ? job.getStartTime() : job.getLastRepeat();
			if (Seconds.secondsBetween(lastTime, LocalDateTime.now()).getSeconds() > SchedulerConfig.KEEPER_JOB_RETRY_INTERVAL) {
				executorService.retryExecute(job);
			}	
		}
	}

}
