package com.predictry.oasis.service;

import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.domain.EmailCampaignTarget;
import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.dto.mail.Product;
import com.predictry.oasis.dto.mail.Recommendation;
import com.predictry.oasis.repository.EmailCampaignRepository;
import com.predictry.oasis.repository.TenantRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import static org.junit.Assert.*;
import javax.jms.JMSException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
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

    @Test
    public void testGetProduct() throws IOException {
        Product product = emailCampaignService.getProduct("latihan", "SECM-NS0029_00002");
        assertNotNull(product);
        assertEquals("SECM-NS0029_00002", product.getId());
        assertEquals("J Sports Japanese Weekly Newspaper Vol 509", product.getName());
        assertEquals("/SECM-NS0029_00002", product.getItem_url());
        assertEquals("/core/media/media.nl?id=80825&c=3713628&h=e397140ba7e4c1ee87dc", product.getImg_url());
        assertEquals(0, product.getPrice().intValue());

        product = emailCampaignService.getProduct("latihan", "SECM-NS0040_00006");
        assertNotNull(product);
        assertEquals("SECM-NS0040_00006", product.getId());
        assertEquals("Nangoku Shimbun Japanese Newspaper 19/02/2015", product.getName());
        assertEquals("/SECM-NS0040_00006", product.getItem_url());
        assertEquals("/core/media/media.nl?id=102320&c=3713628&h=51dc697b64f3a99087aa", product.getImg_url());
        assertEquals(0, product.getPrice().intValue());

        product = emailCampaignService.getProduct("latihan", "unknown123");
        assertNull(product);
    }

    @Test
    public void testGetRecommendation() throws IOException {
        Recommendation recommendation = emailCampaignService.getProductRecommendation("emailtest", "10000", "duo");
        assertNotNull(recommendation);
        assertEquals("duo", recommendation.getAlgo());
        assertEquals(5, recommendation.getItems().size());
        assertTrue(recommendation.getItems().contains("10001"));
        assertTrue(recommendation.getItems().contains("10004"));
        assertTrue(recommendation.getItems().contains("10005"));
        assertTrue(recommendation.getItems().contains("10006"));
        assertTrue(recommendation.getItems().contains("10007"));

        recommendation = emailCampaignService.getProductRecommendation("emailtest", "10004", "duo");
        assertNotNull(recommendation);
        assertEquals("duo", recommendation.getAlgo());
        assertTrue(recommendation.getItems().isEmpty());

        recommendation = emailCampaignService.getProductRecommendation("emailtest", "unknown123", "duo");
        assertNull(recommendation);
    }


    @Test
    public void testProcessTemplate() throws IOException {
        List<String> items = new ArrayList<>();
        items.add("10000");
        items.add("10001");
        items.add("10004");
        items.add("10005");

        EmailCampaign emailCampaign = new EmailCampaign();
        emailCampaign.setTenant(new Tenant("emailtest", "emailtest"));

        // No expression in email
        emailCampaign.setTemplate("test");
        String result = emailCampaignService.processTemplate(emailCampaign, items);
        assertEquals("test", result);

        // Test expression in email
        emailCampaign.setTemplate(StreamUtils.copyToString(getClass().getResourceAsStream("/test_template.html"), Charset.defaultCharset()));
        result = emailCampaignService.processTemplate(emailCampaign, items);
        String expectedResult = StreamUtils.copyToString(getClass().getResourceAsStream("/test_template_result.html"), Charset.defaultCharset());
        assertEquals(expectedResult, result);
    }

}
