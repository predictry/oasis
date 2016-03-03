package com.predictry.oasis.service;

import com.predictry.oasis.dto.mail.MandrillSendRequest;
import com.predictry.oasis.dto.mail.MandrillSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.jms.JMSException;
import java.util.Map;

/**
 * This  class provided email related service.
 *
 * @author jocki
 */
@Service @Profile("prod")
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * This is a listener that receive request to send email.  The message to send email is something like:
     *
     * <code>
     * {
     *   "mandrill_key": "abcdefghijklmnopqrstuvwxyz",
     *   "from": "client@dev.com",
     *   "to": "customer@gmail.com",
     *   "subject": "recommendation from us",
     *   "html_content": "<p>test email</p>"
     * }
     * </code>
     *
     * @param map is extracted from the Json message.
     * @throws JMSException if there is an error while performing JMS operation.
     */
    @JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.EMAIL")
    public void receiveEmailEvents(Map<String, Object> map) throws JMSException {
        String mandrillKey = (String) map.get("mandrill_key");
        String from = (String) map.get("from");
        String to = (String) map.get("to");
        String subject = (String) map.get("subject");
        String html_content = (String) map.get("html_content");
        LOG.info("Receiving email request: from [" + from + "] to [" + to + "]");
        if ((mandrillKey == null) || (from == null) || (to == null) || (subject == null) || (html_content == null)) {
            LOG.warn("Invalid email request message: [" + map + "]");
            return;
        }
        MandrillSendRequest.Recipient recipient = new MandrillSendRequest.Recipient(to);
        MandrillSendRequest.Message message = new MandrillSendRequest.Message(from, recipient, subject, html_content);
        MandrillSendRequest request = new MandrillSendRequest(mandrillKey, message);
        MandrillSendResponse[] response = restTemplate.postForObject("https://mandrillapp.com/api/1.0/messages/send.json", request, MandrillSendResponse[].class);
        if ((response[0].getStatus() != null) && ("error".equals(response[0].getStatus()))) {
            LOG.error("Failed to send email [" + response[0] + "]");
        }
    }

}
