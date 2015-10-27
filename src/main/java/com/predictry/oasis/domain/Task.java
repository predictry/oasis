package com.predictry.oasis.domain;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public Job execute(ObjectMapper objectMapper, ScriptEngineManager scriptEngineManager, String jobId, String tenantId) throws JsonParseException, JsonMappingException, IOException {
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
		
		// Create and return new job
		Job job = new Job(jobId, LocalDateTime.now(), jobPayload.toString());
		job.setPayloadAsMap(payloadAsMap);
		return job; 
	}
	
}
