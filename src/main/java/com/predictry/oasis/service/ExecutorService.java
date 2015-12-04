package com.predictry.oasis.service;

import java.io.IOException;

import javax.script.ScriptEngineManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.Job;
import com.predictry.oasis.repository.ApplicationRepository;
import com.predictry.oasis.repository.JobRepository;
import com.predictry.oasis.util.JsonMessageCreator;

/**
 * This class provides services for task execution.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class ExecutorService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ExecutorService.class);
	
	@Autowired @Qualifier("queue")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ApplicationRepository appRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private EC2Service ec2Service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ScriptEngineManager scriptEngineManager;
	
	public void execute(Long id) throws JsonParseException, JsonMappingException, IOException {
		Application app = appRepository.findOne(id);
		if (app != null) {
			// Launch EC2 instance associated with this service provider
			ec2Service.startInstance(app.getServiceProvider());
			
			// Send messages to the service provider
			app.createJobs(objectMapper, scriptEngineManager).forEach(job -> {
				if (job != null) {
					jmsTemplate.send(app.getQueueName(), new JsonMessageCreator(objectMapper, job.getPayloadAsMap()));
					jobRepository.save(job);
				}
			});
		} else {
			LOG.warn("Can't find application instance for id [" + id + "]");
		}
	}
	
	public void retryExecute(Job job) {
		LOG.info("Retrying job " + job.getName());
		Application app = job.getApplication();
		if (job.retry()) {
			ec2Service.startInstance(app.getServiceProvider());
			jmsTemplate.send(app.getQueueName(), new JsonMessageCreator(objectMapper, job.getPayloadAsMap()));
		} else {
			LOG.warn("Stop retrying job [" + job + "]");
		}
		jobRepository.save(job);
	}
	
}
