package com.predictry.oasis.service;

import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.domain.ServiceProviderStatus;
import com.predictry.oasis.repository.ServiceProviderRepository;

/**
 * This class provides heartbeat related services.
 * 
 * @author jocki
 *
 */
@Service
public class HeartbeatService {
	
	private static final Logger LOG = LoggerFactory.getLogger(HeartbeatService.class);

	@Autowired @Qualifier("topic")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
		
	/**
	 * Send ping message to this service provider to check for the
	 * availability of it.  The communication is asychronous so it will not
	 * wait for the reply.
	 * 
	 * @param serviceProvider is the <code>ServiceProvider</code> to ping.  
	 *        It should be in managed state.
	 */
	@Transactional
	public void ping(ServiceProvider serviceProvider) {
		jmsTemplate.send("OMS.HEARTBEAT", new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage("{\"id\": \"" + 
					serviceProvider.getId().toString() + 
					"\", \"name\": \"" + 
					serviceProvider.getName() + "\"}");
			}
			
		});
	}
	
	/**
	 * Receive heartbeat message from service providers.
	 *  
	 * This JMS Listener that expects message in the following sample:
	 * 
	 * <code>
	 * {
	 *    "name": "SP1",
	 *    "status": "ok"
	 * }
	 * </code>
	 * 
	 * @param map is extracted from the Json message.
	 * @throws JMSException if there is an error while performing JMS operation.
	 */
	@JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.REPLY", 
			selector = "JMSType='heartbeat'")
	public void receiveHeartbeat(Map<String, Object> map) throws JMSException {
		LOG.info("Receiving heartbeat [" + map + "]");
		String spName = (String) map.get("name");
		String status = (String) map.get("status");
		ServiceProvider serviceProvider = serviceProviderRepository.findByNameIgnoreCase(spName);
		if (serviceProvider == null) {
			LOG.warn("Can't find service provider [" + spName + "]");
		} else {
			if ("ok".equals(status)) {
				serviceProvider.setStatus(ServiceProviderStatus.RUNNING);
			} else {
				serviceProvider.setStatus(ServiceProviderStatus.DOWN);
				LOG.warn("[" + serviceProvider + "] is down!");
			}
			serviceProvider.setLastChecked(LocalDateTime.now());
			serviceProviderRepository.save(serviceProvider);
		}
	}
	
	/**
	 * Invoke a REST service call to this service provider to check
	 * for the availability.
	 * 
	 * @param id is the identifier of <code>ServiceProvider</code> to ping for.
	 */
	@Transactional
	public void ping(Long id) {
		ping(serviceProviderRepository.findOne(id));
	}
	
	/**
	 * Check all <code>ServiceProvider</code> to make sure they are working.
	 */
	@Transactional
	public void heartbeat() {
		List<ServiceProvider> serviceProviders = serviceProviderRepository.findAll();
		for (ServiceProvider serviceProvider: serviceProviders) {
			ping(serviceProvider);
		}
	}
}
