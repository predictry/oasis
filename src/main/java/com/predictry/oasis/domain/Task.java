package com.predictry.oasis.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class represent a single task that is part of an <code>Application</code>.
 * 
 * @author jocki
 *
 */
@Entity
public class Task {

	private static final Logger LOG = LoggerFactory.getLogger(Task.class);
	
	private static final Pattern EXP_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
	
	@Id @GeneratedValue
	private Long id;
	
	@NotBlank @Column(length = 2000)
	private String payload;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	@OrderColumn @JoinColumn(name = "task_id")
	private List<Job> jobs = new ArrayList<>();

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public List<Job> getJobs() {
		return jobs;
	}
	
	public List<Job> getJobs(JobStatus status) {
		return jobs.stream().filter(job -> job.getStatus() == status).collect(Collectors.toList());
	}
	
	public Job getJob(String jobId) {
		return getJobs().stream().filter(job -> job.getName().equals(jobId)).findFirst().orElse(null);
	}
	
	public Job getJob(String jobId, JobStatus status) {
		return getJobs().stream().filter(job -> job.getName().equals(jobId) && job.getStatus() == status)
				.findFirst().orElse(null);
	}
	
	public Job jobFinish(String jobId, LocalDateTime finishTime) {
		Job job = getJob(jobId);
		if (job != null) {
			job.finish(finishTime);
		}
		return job;
	}
	
	public Job jobFailed(String jobId, LocalDateTime failTime, String reason) {
		Job job = getJob(jobId);
		if (job != null) {
			job.fail(failTime, reason);
		}
		return job;
	}
	
	public Map<String, Object> execute(ObjectMapper objectMapper, ScriptEngineManager scriptEngineManager, String jobId, String tenantId) throws JsonParseException, JsonMappingException, IOException {
		// Replace expression in payload with real value
		StringBuffer jobPayload = new StringBuffer();
		ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
		if (engine == null) {
			LOG.warn("Can't find 'nashorn' script engine.");
			jobPayload.append(getPayload());
		} else {
			engine.put("CURRENT_DATE", LocalDateTime.now().toString("YYYY-MM-dd"));
			engine.put("CURRENT_HOUR", LocalDateTime.now().toString("HH"));
			Matcher m = EXP_PATTERN.matcher(payload);
			while (m.find()) {
				String exp = m.group(1);
				try {
					m.appendReplacement(jobPayload, String.valueOf(engine.eval(exp)));
				} catch (Exception e) {
					LOG.error("Exception while evaluating [" + exp + "]", e);
				}
			}
			m.appendTail(jobPayload);
		}
		
		// Parsed the payload into Map
		@SuppressWarnings("unchecked")
		Map<String, Object> payloadAsMap = objectMapper.readValue(jobPayload.toString(), Map.class);
		if (payloadAsMap.containsKey("payload") && (payloadAsMap.get("payload") instanceof Map)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> nestedPayload = (Map<String, Object>) payloadAsMap.get("payload");
			nestedPayload.put("tenant", tenantId);
		}
		payloadAsMap.put("jobId", jobId);
		
		// Creating new job
		Job job = new Job(jobId, LocalDateTime.now(), jobPayload.toString());
		jobs.add(job);
		
		// Return result
		return payloadAsMap; 
	}
	
}
