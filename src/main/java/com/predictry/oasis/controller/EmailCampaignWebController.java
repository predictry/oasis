package com.predictry.oasis.controller;

import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.repository.EmailCampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Like <code>EmailCampaingController</code>, this controller also provides services for <code>EmailCampaign</code>,
 * but rather than providing REST APIs, it provides functions that generate views (HTML).
 *
 */
@Controller
@RequestMapping("/email_campaign_web")
public class EmailCampaignWebController {

    @Autowired
    private EmailCampaignRepository emailCampaignRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String list(Model model, Pageable pageable) {
        Page<EmailCampaign> page = emailCampaignRepository.findAll(pageable);
        model.addAttribute("emailCampaigns", page.getContent());
        model.addAttribute("page", page);
        return "emailCampaign/list";
    }

    @RequestMapping(value = "/delete/{id}")
    public String delete(@PathVariable("id") EmailCampaign emailCampaign) {
        emailCampaignRepository.delete(emailCampaign);
        return "redirect:/email_campaign_web";
    }

}
