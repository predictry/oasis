package com.predictry.oasis.service;

import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is a stub for <code>EmailService</code> that didn't actually send email when receiving email request message.
 * To facilitate testing, this class stores received message that can be accessed by calling <code>#getReceivedEvents()</code>.
 */
@Service @Profile("!prod")
public class EmailServiceStub implements EmailService {

    private List<Map<String, Object>> receivedEvents = new ArrayList<>();

    @Override
    @JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.EMAIL")
    public void receiveEmailEvents(Map<String, Object> map) throws JMSException {
        receivedEvents.add(map);
    }

    public List<Map<String, Object>> getReceivedEvents() {
        return receivedEvents;
    }

    public void reset() {
        receivedEvents.clear();
    }

}
