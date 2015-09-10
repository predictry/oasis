package com.predictry.oasis.domain.metric;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDateTime;

/**
 * This class represents metric for storage used for recommendation (in bytes).
 * @author jocki
 *
 */
public class StorageMetric {

	private LocalDateTime time;
	
	private Map<String, Double> storage = new HashMap<>();

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Map<String, Double> getStorage() {
		return storage;
	}
	
	public void addStorage(String type, Double value) {
		this.storage.put(type, value);
	}
	
}
