package com.predictry.oasis.controller;

import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.service.EmailCampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * This API provides functions related to email campaign.
 */
@Controller
@RequestMapping("/email_campaign")
public class EmailCampaignController {

    private static final Logger LOG = LoggerFactory.getLogger(EmailCampaignController.class);

    @Autowired
    private EmailCampaignService emailCampaignService;

    @RequestMapping(value = "/{tenant}", method = RequestMethod.POST)
    public @ResponseBody  Map<String, String> create(@RequestBody EmailCampaign emailCampaign, @PathVariable("tenant") Tenant tenant) {
        LOG.info("Creating new campaign [" + emailCampaign.getCampaignName() + "] for tenant [" + tenant.getName() + "]");
        Map<String, String> result = new HashMap<>();
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
        return result;
    }

}
