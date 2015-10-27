package com.predictry.oasis.service;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.predictry.oasis.domain.Job;
import com.predictry.oasis.repository.JobRepository;
import com.predictry.oasis.service.handler.StatusHandler;

/**
 * This class provide services related to <code>Job</code>.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class JobService {

	private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private List<StatusHandler> statusHandlers;
	
	@JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.STATUS")
	public void receiveStatus(Map<String, Object> map) throws JMSException {
		LOG.info("Receiving status [" + map + "]");
		if (!map.containsKey("serviceProvider") || !map.containsKey("jobId")) {
			LOG.warn("Encountered invalid message, it will be ignored!");
			return;
		}
		Job job = jobRepository.findByName((String) map.get("jobId"));
		if (job != null) {
			for (StatusHandler statusHandler: statusHandlers) {
				statusHandler.handle(job, map);
			}
		} else {
			LOG.warn("Encountered invalid job id [" + (String) map.get("jobId") + "]");
		}
	}
	
}
