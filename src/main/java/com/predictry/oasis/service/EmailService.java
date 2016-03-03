package com.predictry.oasis.service;

import javax.jms.JMSException;
import java.util.Map;

/**
 * This is the interface that provides operations to listen for sending email request.
 */
@SuppressWarnings("unused")
public interface EmailService {

    void receiveEmailEvents(Map<String, Object> map) throws JMSException;

}
