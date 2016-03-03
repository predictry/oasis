package com.predictry.oasis.repository;

import com.predictry.oasis.domain.EmailCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to manage <code>EmailCampaign</code>.
 */
@Repository
public interface EmailCampaignRepository extends JpaRepository<EmailCampaign, Long> {
}
