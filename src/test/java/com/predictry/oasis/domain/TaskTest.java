package com.predictry.oasis.domain;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.script.ScriptEngineManager;

import org.joda.time.LocalDateTime;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TaskTest {

	@Test
	public void testExpressionInPayload() throws JsonParseException, JsonMappingException, IOException {
		String payload = "{" +
			"\"type\": \"compute-recommendation\"," +
			"\"timeout\": 1000," +
			"\"payload\": {" +
			"\"id\": 10," +
			"\"name\": \"my name\"," +
			"\"value1\": \"test ${CURRENT_DATE}\"," +
			"\"value2\": \"test ${CURRENT_DATE} ${CURRENT_HOUR}\"," +
			"\"value3\": \"${1+1}\"" +
			"}}";
		Task task = new Task();
		task.setPayload(payload);
		
		ObjectMapper objectMapper = new ObjectMapper();
		ScriptEngineManager manager = new ScriptEngineManager();
		Map<String, Object> parsedPayload = task.parsePayload(objectMapper, manager);
		assertEquals("compute-recommendation", parsedPayload.get("type"));
		assertEquals(1000, parsedPayload.get("timeout"));
		@SuppressWarnings("unchecked")
		Map<String, Object> nestedPayload = (Map<String, Object>) parsedPayload.get("payload"); 
		assertEquals(10, nestedPayload.get("id"));
		assertEquals("my name", nestedPayload.get("name"));
		assertEquals("test " + LocalDateTime.now().toString("YYYY-MM-dd"), nestedPayload.get("value1"));
		assertEquals("test " + LocalDateTime.now().toString("YYYY-MM-dd HH"), nestedPayload.get("value2"));
		assertEquals("2", nestedPayload.get("value3"));
	}
	
}
