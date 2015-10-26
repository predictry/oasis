package com.predictry.oasis.domain;

/**
 * <code>JobStatus</code> determines the status of <code>Job</code>.
 * 
 * @author jocki
 *
 */
public enum JobStatus {

	STARTED("Started", true), FINISH("Finish", false), FAIL("Fail", false), REPEAT("Repeat", true);
	
	private String description;
	private boolean running;

	private JobStatus(String description, boolean running) {
		this.description = description;
		this.running = running;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isRunning() {
		return running;
	}

	public String toString() {
		return description;
	}
	
}
