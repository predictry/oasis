package com.predictry.oasis.service;

import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.domain.EmailCampaignTarget;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.repository.EmailCampaignRepository;
import com.predictry.oasis.repository.TenantRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;
import javax.jms.JMSException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailCampaignServiceTest extends TestCase {

    @Autowired
    private EmailCampaignService emailCampaignService;

    @Autowired
    private EmailServiceStub emailServiceStub;

    @Autowired
    private EmailCampaignRepository emailCampaignRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Before
    public void setup() {
        emailServiceStub.reset();
    }

    @Test
    public void testProcessEmailCampaign() throws JMSException {
        Tenant tenant1 = new Tenant("latihan", "latihan");
        tenant1 = tenantRepository.saveAndFlush(tenant1);

        EmailCampaign emailCampaign = new EmailCampaign();
        emailCampaign.setTenant(tenant1);
        emailCampaign.setCampaignName("campaign1");
        emailCampaign.setEmailFrom("from@from.com");
        emailCampaign.setEmailSubject("subject recommendation");
        emailCampaign.setMandrillAPIKey("apikey1");
        emailCampaign.setPongoUserId("user1");
        emailCampaign.setTemplate("<p>template1</p>");
        emailCampaign.addTarget("buy", 7);
        emailCampaign = emailCampaignRepository.saveAndFlush(emailCampaign);

        emailCampaignService.processEmailCampaign(emailCampaign, "buy", LocalDate.parse("2016-02-15"));

        // Since sending email is async, wait for a while
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check messages sent to stub queue
        List<Map<String,Object>> messages = emailServiceStub.getReceivedEvents();
        assertEquals(4, messages.size());

        Map<String,Object> mailMessage = messages.get(0);
        assertEquals("apikey1", mailMessage.get("mandrill_key"));
        assertEquals("from@from.com", mailMessage.get("from"));
        assertEquals("user1@gmail.com", mailMessage.get("to"));
        assertEquals("subject recommendation", mailMessage.get("subject"));
        assertEquals("<p>template1</p>", mailMessage.get("html_content"));

        mailMessage = messages.get(1);
        assertEquals("apikey1", mailMessage.get("mandrill_key"));
        assertEquals("from@from.com", mailMessage.get("from"));
        assertEquals("user2@gmail.com", mailMessage.get("to"));
        assertEquals("subject recommendation", mailMessage.get("subject"));
        assertEquals("<p>template1</p>", mailMessage.get("html_content"));

        mailMessage = messages.get(2);
        assertEquals("apikey1", mailMessage.get("mandrill_key"));
        assertEquals("from@from.com", mailMessage.get("from"));
        assertEquals("user3@gmail.com", mailMessage.get("to"));
        assertEquals("subject recommendation", mailMessage.get("subject"));
        assertEquals("<p>template1</p>", mailMessage.get("html_content"));

        mailMessage = messages.get(3);
        assertEquals("apikey1", mailMessage.get("mandrill_key"));
        assertEquals("from@from.com", mailMessage.get("from"));
        assertEquals("user4@gmail.com", mailMessage.get("to"));
        assertEquals("subject recommendation", mailMessage.get("subject"));
        assertEquals("<p>template1</p>", mailMessage.get("html_content"));
    }

    @Test
    public void testProcessEmailCampaignWithEmptyItems() throws JMSException {
        Tenant tenant1 = new Tenant("latihan", "latihan");
        tenant1 = tenantRepository.saveAndFlush(tenant1);

        EmailCampaign emailCampaign = new EmailCampaign();
        emailCampaign.setTenant(tenant1);
        emailCampaign.setCampaignName("campaign1");
        emailCampaign.setEmailFrom("from@from.com");
        emailCampaign.setEmailSubject("subject recommendation");
        emailCampaign.setMandrillAPIKey("apikey1");
        emailCampaign.setPongoUserId("user1");
        emailCampaign.setTemplate("<p>template1</p>");
        emailCampaign.addTarget("view", 7);
        emailCampaign = emailCampaignRepository.saveAndFlush(emailCampaign);

        emailCampaignService.processEmailCampaign(emailCampaign, "view", LocalDate.parse("2016-02-17"));

        // Since sending email is async, wait for a while
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check messages sent to stub queue
        List<Map<String,Object>> messages = emailServiceStub.getReceivedEvents();
        assertEquals(1, messages.size());

        Map<String,Object> mailMessage = messages.get(0);
        assertEquals("apikey1", mailMessage.get("mandrill_key"));
        assertEquals("from@from.com", mailMessage.get("from"));
        assertEquals("user1@gmail.com", mailMessage.get("to"));
        assertEquals("subject recommendation", mailMessage.get("subject"));
        assertEquals("<p>template1</p>", mailMessage.get("html_content"));
    }

}
