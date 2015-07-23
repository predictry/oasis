package com.predictry.oasis.service;

import java.util.Map;

import javax.transaction.Transactional;

import org.joda.time.LocalDateTime;
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

	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * Invoke a REST service call to this service provider to check
	 * for the availability of its availability.
	 * 
	 * @param serviceProvider is the serviceProvider to ping.  It should be in 
	 *        managed state
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
				serviceProvider.setStatus(ServiceProviderStatus.DOWN);
			}
		} catch (Exception ex) {
			serviceProvider.setStatus(ServiceProviderStatus.DOWN);
		}
		serviceProvider.setLastChecked(LocalDateTime.now());
		serviceProviderRepository.save(serviceProvider);
		return ok;
	}
	
	@Transactional
	public boolean ping(Long id) {
		return ping(serviceProviderRepository.findOne(id));
	}
}
