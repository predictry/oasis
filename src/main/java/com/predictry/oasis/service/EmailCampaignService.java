package com.predictry.oasis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.oasis.domain.EmailCampaign;
import com.predictry.oasis.repository.EmailCampaignRepository;
import com.predictry.oasis.util.JsonMessageCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provide services related to <code>EmailCampaign</code>.
 */
@Service
@Transactional
public class EmailCampaignService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailCampaignService.class);

    @Autowired
    private EmailCampaignRepository emailCampaignRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired @Qualifier("queue")
    private JmsTemplate jmsTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public EmailCampaign save(EmailCampaign emailCampaign) {
        emailCampaign = emailCampaignRepository.saveAndFlush(emailCampaign);
        Map<String, String> message = new HashMap<>();
        message.put("id", emailCampaign.getId().toString());
        jmsTemplate.send("OMS.EMAIL_CAMPAIGN", new JsonMessageCreator(objectMapper, message));
        return emailCampaign;
    }

    @JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.EMAIL_CAMPAIGN")
    public void receiveEmailCampaignEvents(Map<String, Object> map) throws JMSException {
        Long campaignId = Long.valueOf(map.get("id").toString());
        LOG.info("Processing email campaign [" + campaignId + "]");
        EmailCampaign emailCampaign = emailCampaignRepository.findOne(campaignId);
        emailCampaign.getTargets().forEach(target -> processEmailCampaign(emailCampaign, target.getAction().toLowerCase(), target.targetDate()));
    }

    public void processEmailCampaign(EmailCampaign emailCampaign, String action, LocalDate targetDate) {
        String prefix = String.format("data/tenants/%s/history/%d/%02d/%02d/", emailCampaign.getTenant().getId(), targetDate.getYear(), targetDate.getMonthValue(), targetDate.getDayOfMonth());
        s3Service.listBucket("predictry", prefix).forEach(key -> {
            try {
                Map json = objectMapper.readValue(s3Service.read("predictry", key), Map.class);
                if (json.containsKey("email")) {
                    // Check last action for that user
                    String userId = key.substring(key.lastIndexOf('/') + 1, key.length() - 5);
                    String uri = String.format("http://fisher.predictry.com:8090/fisher/userprofile/%s/%s/%s", emailCampaign.getTenant().getId(), userId, action);
                    Map userProfile = restTemplate.getForObject(uri, Map.class);
                    if (userProfile.containsKey("lastAction")) {
                        LocalDateTime lastAction = (LocalDateTime) userProfile.get("lastAction");
                        if (lastAction.isAfter(targetDate.atStartOfDay())) {
                            return;
                        }
                    }

                    List items = null;
                    if ("buy".equals(action)) {
                        items = (List) json.get("buys");
                    } else if ("view".equals(action)) {
                        items = (List) json.get("views");
                    }
                    // Don't send email if no item in history
                    if ((items == null) || items.isEmpty()) {
                        return;
                    }

                    // TODO: Process email template later!

                    // Send email for that user
                    Map<String, String> message = new HashMap<>();
                    message.put("mandrill_key", emailCampaign.getMandrillAPIKey());
                    message.put("from", emailCampaign.getEmailFrom());
                    message.put("to", (String) json.get("email"));
                    message.put("subject", emailCampaign.getEmailSubject());
                    message.put("html_content", emailCampaign.getTemplate());
                    jmsTemplate.send("OMS.EMAIL", new JsonMessageCreator(objectMapper, message));
                }
            } catch (IOException ex) {
                LOG.error("Error while parsing Json [" + key + "]", ex);
            }
        });
    }

}
