package com.predictry.oasis.domain;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
@Embeddable
public class Task {

	private static final Logger LOG = LoggerFactory.getLogger(Task.class);
	
	private static final Pattern EXP_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");
	
	@NotBlank @Column(length = 2000)
	private String payload;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public Map<String, Object> parsePayload(ObjectMapper objectMapper, ScriptEngineManager scriptEngineManager) throws JsonParseException, JsonMappingException, IOException {
		@SuppressWarnings("unchecked")
		Map<String, Object> payloadAsMap = objectMapper.readValue(payload, Map.class);
		ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
		if (engine == null) {
			LOG.warn("Can't find 'nashorn' script engine.");
		} else {
			engine.put("CURRENT_DATE", LocalDateTime.now().toString("YYYY-MM-dd"));
			engine.put("CURRENT_HOUR", LocalDateTime.now().toString("HH"));
			processExpression(payloadAsMap, engine);
		}
		return payloadAsMap;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void processExpression(Map<String, Object> map, ScriptEngine engine) {
		map.replaceAll((k, v) -> {
			if (v instanceof String) {
				StringBuffer result = new StringBuffer();
				Matcher m = EXP_PATTERN.matcher((String) v);
				while (m.find()) {
					String exp = m.group(1);
					try {
						m.appendReplacement(result, engine.eval(exp).toString());
					} catch (Exception e) {
						LOG.error("Exception while evaluating [" + exp + "]", e);
					}
				}
				m.appendTail(result);
				return result.toString();
			} else if (v instanceof Map) {
				processExpression((Map) v, engine);
				return v;
			} else {
				return v;
			}
		});
	}
	
}
