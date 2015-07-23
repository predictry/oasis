package com.predictry.oasis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.predictry.oasis.domain.ServiceProvider;

/**
 * CRUD repository for ServiceProvider.
 * 
 * @author jocki
 *
 */
@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

}
