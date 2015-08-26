package com.predictry.oasis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.predictry.oasis.domain.Tenant;

/**
 * CRUD Repository for <code>Tenant</code>.
 * 
 * @author jocki
 *
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

}
