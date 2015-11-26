package com.predictry.oasis.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.repository.ServiceProviderRepository;

/**
 * This class provides heartbeat related services.
 * 
 * @author jocki
 *
 */
@Service
public class HeartbeatService {
	
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
