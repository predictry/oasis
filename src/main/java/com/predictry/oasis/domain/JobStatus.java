package com.predictry.oasis.domain;

/**
 * <code>JobStatus</code> determines the status of <code>Job</code>.
 * 
 * @author jocki
 *
 */
public enum JobStatus {

	STARTED("Started", true, "text-primary"), 
	FINISH("Finish", false, "text-success"), 
	FAIL("Fail", false, "text-danger"), 
	REPEAT("Repeat", true, "text-warning");
	
	private String description;
	private boolean running;
	private String cssClass;

	private JobStatus(String description, boolean running, String cssClass) {
		this.description = description;
		this.running = running;
		this.cssClass = cssClass;
	}
	
	public String getDescription() {
		return description;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public String getCssClass() {
		return cssClass;
	}

	public String toString() {
		return description;
	}
	
}
