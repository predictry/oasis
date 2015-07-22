package com.predictry.oasis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.predictry.oasis.domain.ServiceProvider;

/**
 * CRUD repository for ServiceProvider.
 * 
 * @author jocki
 *
 */
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

}
