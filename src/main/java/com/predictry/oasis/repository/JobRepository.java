package com.predictry.oasis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.predictry.oasis.domain.Job;

/**
 * CRUD repository for <code>Job</code>.
 * 
 * @author jocki
 *
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long>  {

}
