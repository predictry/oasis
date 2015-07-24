package com.predictry.oasis.service;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * Invoke a REST service call to this service provider to check
	 * for the availability of its availability.
	 * 
	 * @param serviceProvider is the <code>ServiceProvider</code> to ping.  It should 
	 *        be in managed state.
	 *        
	 * @return <code>true</code> if this service provider is running.
	 */
	@Transactional
	public boolean ping(ServiceProvider serviceProvider) {
		RestTemplate restTemplate = new RestTemplate();
		String url = serviceProvider.getBaseUrl() + ServiceProvider.URI_HEARTBEAT;
		boolean ok = false;
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> results = restTemplate.getForObject(url, Map.class);
			if (results.containsKey("ok")) {
				ok = (boolean) results.get("ok");
				serviceProvider.setStatus(ok ? ServiceProviderStatus.RUNNING : 
					ServiceProviderStatus.DOWN);
			} else {
				LOG.warn("[" + serviceProvider + "] is down!");
				serviceProvider.setStatus(ServiceProviderStatus.DOWN);
			}
		} catch (Exception ex) {
			LOG.warn("[" + serviceProvider + "] is down!");
			serviceProvider.setStatus(ServiceProviderStatus.DOWN);
		}
		serviceProvider.setLastChecked(LocalDateTime.now());
		serviceProviderRepository.save(serviceProvider);
		return ok;
	}
	
	/**
	 * Invoke a REST service call to this service provider to check
	 * for the availability.
	 * 
	 * @param id is the identifier of <code>ServiceProvider</code> to ping for.
	 * 
	 * @return <code>true</code> if this service provider is running.
	 */
	@Transactional
	public boolean ping(Long id) {
		return ping(serviceProviderRepository.findOne(id));
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
