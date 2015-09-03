package com.predictry.oasis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.predictry.oasis.domain.Application;

/**
 * CRUD Repository for <code>Application</code>.
 * 
 * @author jocki
 *
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
	
	Application findByNameIgnoreCase(String name);

}
