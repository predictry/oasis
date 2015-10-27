package com.predictry.oasis.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Map;

import javax.script.ScriptEngineManager;

import org.joda.time.LocalDate;
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
			"\"value3\": ${1+1}" +
			"}}";
		
		Task task = new Task();
		task.setPayload(payload);
		
		ObjectMapper objectMapper = new ObjectMapper();
		ScriptEngineManager manager = new ScriptEngineManager();
		Job job = task.execute(objectMapper, manager, "job1", "tenant1");
		Map<String, Object> parsedPayload = job.getPayloadAsMap();
		assertEquals("compute-recommendation", parsedPayload.get("type"));
		assertEquals("job1", parsedPayload.get("jobId"));
		assertEquals(1000, parsedPayload.get("timeout"));
		@SuppressWarnings("unchecked")
		Map<String, Object> nestedPayload = (Map<String, Object>) parsedPayload.get("payload");
		assertEquals("tenant1", nestedPayload.get("tenant"));
		assertEquals(10, nestedPayload.get("id"));
		assertEquals("my name", nestedPayload.get("name"));
		assertEquals("test " + LocalDateTime.now().toString("YYYY-MM-dd"), nestedPayload.get("value1"));
		assertEquals("test " + LocalDateTime.now().toString("YYYY-MM-dd HH"), nestedPayload.get("value2"));
		assertEquals(2, nestedPayload.get("value3"));
		
		// Make sure job is created		
		assertEquals("job1", job.getName());
		assertEquals("{" +
			"\"type\": \"compute-recommendation\"," +
			"\"timeout\": 1000," +
			"\"payload\": {" +
			"\"id\": 10," +
			"\"name\": \"my name\"," +
			"\"value1\": \"test " + LocalDateTime.now().toString("YYYY-MM-dd") + "\"," +
			"\"value2\": \"test " + LocalDateTime.now().toString("YYYY-MM-dd HH") + "\"," +
			"\"value3\": 2" +
			"}}", job.getPayload());
		assertNotNull(job.getStartTime());
		assertEquals(LocalDate.now(), job.getStartTime().toLocalDate());
		assertEquals(JobStatus.STARTED, job.getStatus());
	}
	
}
