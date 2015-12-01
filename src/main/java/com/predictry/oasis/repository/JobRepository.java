package com.predictry.oasis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.predictry.oasis.domain.Job;
import com.predictry.oasis.domain.JobStatus;
import com.predictry.oasis.domain.ServiceProvider;

/**
 * CRUD repository for <code>Job</code>.
 * 
 * @author jocki
 *
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long>  {
	
	public Job findByName(String jobId);
	
	public List<Job> findByStatusIn(List<JobStatus> jobStatuses);
	
	public List<Job> findByApplicationServiceProvider(ServiceProvider serviceProvider);

}
