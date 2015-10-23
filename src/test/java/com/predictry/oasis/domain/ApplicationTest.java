package com.predictry.oasis.domain;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDateTime;
import org.junit.Test;

public class ApplicationTest {

	@Test
	public void testGenerateJobId() {
		Application app = new Application();
		app.setName("sample_app");
		Task task1 = new Task();
		Task task2 = new Task();
		Task task3 = new Task();
		app.addTask(task1);
		app.addTask(task2);
		app.addTask(task3);
		String timePortion = LocalDateTime.now().toString("YYYY-MM-dd_HH:mm");
		assertEquals("sample_app_0_" + timePortion, app.generateJobId(task1));
		assertEquals("sample_app_1_" + timePortion, app.generateJobId(task2));
		assertEquals("sample_app_2_" + timePortion, app.generateJobId(task3));
	}
	
}
