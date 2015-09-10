package com.predictry.oasis.domain.metric;

import org.joda.time.LocalDateTime;

/**
 * This class represents metric for recommendation download speed (in milliseconds).
 * 
 * @author jocki
 *
 */
public class SpeedMetric {
	
	private LocalDateTime time;
	
	private Long speed;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public Long getSpeed() {
		return speed;
	}

	public void setSpeed(Long speed) {
		this.speed = speed;
	}
	
}
