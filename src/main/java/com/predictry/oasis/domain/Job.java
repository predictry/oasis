package com.predictry.oasis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.LocalDateTime;

/**
 * When application run a <code>Task</code>, it will create an instance of <code>Job</code> for that <code>Task</code>.
 * A single <code>Task</code> can have multiple <code>Task</code>s.
 * 
 * @author jocki
 *
 */
@Entity
public class Job {

	@Id @GeneratedValue
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotNull @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime startTime;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime endTime;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	private LocalDateTime lastRepeat;
	
	private Integer numOfRepeat = 0;
	
	@NotNull
	private JobStatus status = JobStatus.STARTED;
	
	@Column(length = 2000)
	private String reason;
	
	@NotBlank
	private String payload;
	
	public Job() { }
	
	public Job(String name, LocalDateTime startTime, String payload) {
		this.name = name;
		this.startTime = startTime;
		this.payload = payload;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}
	
	public LocalDateTime getLastRepeat() {
		return lastRepeat;
	}

	public void setLastRepeat(LocalDateTime lastRepeat) {
		this.lastRepeat = lastRepeat;
	}

	public Integer getNumOfRepeat() {
		return numOfRepeat;
	}

	public void setNumOfRepeat(Integer numOfRepeat) {
		this.numOfRepeat = numOfRepeat;
	}

	public JobStatus getStatus() {
		return status;
	}

	public void setStatus(JobStatus status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public void finish(LocalDateTime finishTime) {
		this.status = JobStatus.FINISH;
		this.endTime = finishTime;
	}
	
	public void fail(LocalDateTime failTime, String reason) {
		this.status = JobStatus.FAIL;
		this.endTime = failTime;
		this.reason = reason;
	}
	
}
