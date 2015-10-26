package com.predictry.oasis.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
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
		Map<String, Object> parsedPayload = task.execute(objectMapper, manager, "job1", "tenant1");
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
		assertEquals(1, task.getJobs().size());
		Job job = task.getJobs().get(0);
		
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
	
	@Test
	public void testGetJobs() throws JsonParseException, JsonMappingException, IOException {
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
		ObjectMapper objectMapper = new ObjectMapper();
		ScriptEngineManager manager = new ScriptEngineManager();	
		Task task = new Task();
		task.setPayload(payload);
		task.execute(objectMapper, manager, "job1", "tenant1");
		task.execute(objectMapper, manager, "job2", "tenant2");
		task.execute(objectMapper, manager, "job3", "tenant3");
		
		// Check get by state
		List<Job> jobs = task.getJobs(JobStatus.STARTED);
		assertEquals(3, jobs.size());
		assertTrue(jobs.contains(task.getJob("job1")));
		assertTrue(jobs.contains(task.getJob("job2")));
		assertTrue(jobs.contains(task.getJob("job3")));
		assertTrue(jobs.contains(task.getJob("job1", JobStatus.STARTED)));
		assertTrue(jobs.contains(task.getJob("job2", JobStatus.STARTED)));
		assertTrue(jobs.contains(task.getJob("job3", JobStatus.STARTED)));
		assertNotNull(task.getJob("job1").getStartTime());
		assertNotNull(task.getJob("job2").getStartTime());
		assertNotNull(task.getJob("job3").getStartTime());
		assertNull(task.getJob("job4"));
		assertNull(task.getJob("job4", JobStatus.STARTED));
		assertNull(task.getJob("job1", JobStatus.REPEAT));
	}
	
	@Test
	public void testJobFinish() throws JsonParseException, JsonMappingException, IOException {
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
		ObjectMapper objectMapper = new ObjectMapper();
		ScriptEngineManager manager = new ScriptEngineManager();
		Task task = new Task();
		task.setPayload(payload);
		task.execute(objectMapper, manager, "job1", "tenant1");
		task.execute(objectMapper, manager, "job2", "tenant2");
		task.execute(objectMapper, manager, "job3", "tenant3");
		
		// Finish "job1"
		LocalDateTime finishTime = LocalDateTime.now();
		task.jobFinish("job1", finishTime);
		assertEquals(JobStatus.FINISH, task.getJob("job1").getStatus());
		assertEquals(finishTime, task.getJob("job1").getEndTime());
		assertEquals(1, task.getJobs(JobStatus.FINISH).size());
		assertEquals(2, task.getJobs(JobStatus.STARTED).size());
		
		// Finish "job2"
		task.jobFinish("job2", finishTime);
		assertEquals(JobStatus.FINISH, task.getJob("job2").getStatus());
		assertEquals(finishTime, task.getJob("job2").getEndTime());
		assertEquals(2, task.getJobs(JobStatus.FINISH).size());
		assertEquals(1, task.getJobs(JobStatus.STARTED).size());
	}
	
	@Test
	public void testJobFailed() throws JsonParseException, JsonMappingException, IOException {
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
		ObjectMapper objectMapper = new ObjectMapper();
		ScriptEngineManager manager = new ScriptEngineManager();
		Task task = new Task();
		task.setPayload(payload);
		task.execute(objectMapper, manager, "job1", "tenant1");
		task.execute(objectMapper, manager, "job2", "tenant2");
		task.execute(objectMapper, manager, "job3", "tenant3");
		
		// Failed "job1"
		LocalDateTime failTime = LocalDateTime.now();
		task.jobFailed("job1", failTime, "job1 fail reason");
		assertEquals(JobStatus.FAIL, task.getJob("job1").getStatus());
		assertEquals("job1 fail reason", task.getJob("job1").getReason());
		assertEquals(failTime, task.getJob("job1").getEndTime());
		assertEquals(1, task.getJobs(JobStatus.FAIL).size());
		assertEquals(2, task.getJobs(JobStatus.STARTED).size());
		
		// Finish "job2"
		task.jobFailed("job2", failTime, "job2 fail reason");
		assertEquals(JobStatus.FAIL, task.getJob("job2").getStatus());
		assertEquals("job2 fail reason", task.getJob("job2").getReason());
		assertEquals(failTime, task.getJob("job2").getEndTime());
		assertEquals(2, task.getJobs(JobStatus.FAIL).size());
		assertEquals(1, task.getJobs(JobStatus.STARTED).size());
	}
	
}
