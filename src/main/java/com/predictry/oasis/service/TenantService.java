package com.predictry.oasis.service;

import java.util.Map;

import javax.jms.JMSException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.domain.TenantStatus;
import com.predictry.oasis.repository.TenantRepository;

/**
 * This class provided tenant related service including synchronization.
 * 
 * @author jocki
 *
 */
@Service
@Transactional
public class TenantService {

	private static final Logger LOG = LoggerFactory.getLogger(TenantService.class);
	
	@Autowired
	private TenantRepository tenantRepository;
	
	/**
	 * Receive tenant related events such as <code>new</code>, <code>disable</code>,
	 * <code>enable</code>, and <code>delete</code>.
	 * 
	 * This JMS Listener expects message such as:
	 * 
	 * <code>
	 * {
	 *    "tenantId": "tenant1",
	 *    "action": "new"
	 * }
	 * </code>
	 * 
	 * @param map is extracted from the Json message.
	 * @throws JMSException if there is an error while perfoming JMS operation.
	 */
	@JmsListener(containerFactory = "queueJmsListenerContainerFactory", destination = "OMS.TENANT")
	public void receiveTenantEvents(Map<String, Object> map) throws JMSException {
		LOG.info("Receiving tenant events [" + map + "]");
		String tenantId = (String) map.get("tenantId");
		String action = (String) map.get("action");
		if ((tenantId == null) || (action == null)) {
			LOG.warn("Invalid tenant events [" + map + "]");
			return;
		}
		Tenant tenant;
		switch (action.toLowerCase()) {
			case "new":
				if (tenantRepository.findOne(tenantId) == null) {
					tenant = new Tenant(tenantId, tenantId);
					tenantRepository.save(tenant);
				} else {
					LOG.warn("Ignored existing tenant id [" + tenantId + "] for new action");
				}
				break;
			case "delete":
				tenant = tenantRepository.findOne(tenantId);
				if (tenant != null) {
					tenantRepository.delete(tenant);
				}
				break;
			case "enable":
				tenant = tenantRepository.findOne(tenantId);
				tenant.setStatus(TenantStatus.ENABLED);
				break;
			case "disable":
				tenant = tenantRepository.findOne(tenantId);
				tenant.setStatus(TenantStatus.DISABLED);
				break;
			default:
				LOG.warn("Received invalid action [" + action + "]");
		}
	}
	
}
