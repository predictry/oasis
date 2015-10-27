package com.predictry.oasis.repository;

import java.util.List;

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
	
	List<Application> findByServiceProviderName(String serviceProvider);

}
