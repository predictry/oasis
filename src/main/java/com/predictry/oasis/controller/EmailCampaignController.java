package com.predictry.oasis.controller;

import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.service.EmailCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * This API provides functions related to email campaign.
 */
@RestController
@RequestMapping("/email_campaign")
public class EmailCampaignController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailCampaignController.class);

    @Autowired
    private EmailCampaignService emailCampaignService;

    @RequestMapping(value = "/{tenant}", method = RequestMethod.POST)
    public  Map<String, String> create(@RequestBody EmailCampaign emailCampaign, @PathVariable("tenant") Tenant tenant) {
        Map<String, String> result = new HashMap<>();
        if (tenant == null) {
            LOG.error("Can't create new campaign because can't find tenant id.");
            result.put("status", "error");
            result.put("message", "Tenant id is not found");
        } else {
            LOG.info("Creating new campaign [" + emailCampaign.getCampaignName() + "] for tenant [" + tenant.getName() + "]");
            try {
                emailCampaign.setTenant(tenant);
                emailCampaign = emailCampaignService.save(emailCampaign);
                result.put("status", "created");
                result.put("id", emailCampaign.getId().toString());
                LOG.info("Campaign with id [" + emailCampaign.getId() + "] is created");
            } catch (Exception ex) {
                result.put("status", "error");
                result.put("message", ex.getMessage());
                LOG.error("Error while creating new campaign", ex);
            }
        }
        return result;
    }

    @RequestMapping(value = "/{campaignId}", method = RequestMethod.GET)
    public EmailCampaign get(@PathVariable("campaignId") EmailCampaign emailCampaign) {
        if (emailCampaign == null) {
            throw new EntityNotFoundException("Can't find email campaign");
        }
        return emailCampaign;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, String> handleError(Exception exception) {
        LOG.error("Encountered error while processing email campaign", exception);
        Map<String, String> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", "Unknown error: " + exception.getMessage());
        return result;
    }

}
