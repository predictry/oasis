package com.predictry.oasis.util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Use this implementation of <code>MessageCreator</code> to generate Json text message 
 * from an existing object.
 * 
 * @author jocki
 *
 */
public class JsonMessageCreator implements MessageCreator {

	private static final Logger LOG = LoggerFactory.getLogger(JsonMessageCreator.class);
	
	private ObjectMapper objectMapper;
	private Object object;
	
	public JsonMessageCreator(ObjectMapper objectMapper, Object object) {
		this.objectMapper = objectMapper;
		this.object = object;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		try {
			return session.createTextMessage(objectMapper.writeValueAsString(object));
		} catch (JsonProcessingException e) {
			LOG.error("Can't convert object (" + object + ") into Json", e);
			return null;
		}
	}
	
}
