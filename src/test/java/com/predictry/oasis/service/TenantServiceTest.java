package com.predictry.oasis.service;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.domain.TenantStatus;
import com.predictry.oasis.repository.TenantRepository;

public class TenantServiceTest extends TestCase {

	@Autowired
	private TenantService tenantService;
	
	@Autowired
	private TenantRepository tenantRepository;
	
	@Test
	public void testReceiveNewTenantEvent() throws JMSException {
		// Check to make sure tenant id doesn't exists
		assertNull(tenantRepository.findOne("tenant1"));
		
		// Send messsage
		Map<String, Object> message = new HashMap<>();
		message.put("tenantId", "tenant1");
		message.put("action", "new");
		tenantService.receiveTenantEvents(message);
		
		// Check if new tenant is created
		Tenant tenant = tenantRepository.findOne("tenant1");
		assertEquals("tenant1", tenant.getId());
		assertEquals("tenant1", tenant.getName());
		assertEquals(TenantStatus.ENABLED, tenant.getStatus());
	}
	
	@Test
	public void testReceiveDuplicateNewTenantEvent() throws JMSException {
		// Create new tenant
		Tenant tenant = new Tenant("abc", "def");
		tenantRepository.save(tenant);
		assertNotNull(tenantRepository.findOne("abc"));
		
		// Send message
		Map<String, Object> message = new HashMap<>();
		message.put("tenantId", "abc");
		message.put("action", "new");
		tenantService.receiveTenantEvents(message);
		
		// New tenant event should be rejected and nothing was updated
		tenant = tenantRepository.findOne("abc");
		assertEquals("abc", tenant.getId());
		assertEquals("def", tenant.getName());
	}
	
	@Test
	public void testReceiveDeleteTenantEvent() throws JMSException {
		// Create new tenant
		Tenant tenant = new Tenant("abc", "def");
		tenantRepository.save(tenant);
		assertNotNull(tenantRepository.findOne("abc"));
		
		// Send message
		Map<String, Object> message = new HashMap<>();
		message.put("tenantId", "abc");
		message.put("action", "delete");
		tenantService.receiveTenantEvents(message);
		
		// Check if tenant is deleted
		assertNull(tenantRepository.findOne("abc"));
	}
	
	@Test
	public void testReceiveDisableTenantEvent() throws JMSException {
		// Create new tenant
		Tenant tenant = new Tenant("abc", "def");
		tenantRepository.save(tenant);
		assertNotNull(tenantRepository.findOne("abc"));
		
		// Send message
		Map<String, Object> message = new HashMap<>();
		message.put("tenantId", "abc");
		message.put("action", "disable");
		tenantService.receiveTenantEvents(message);
		
		// Check if tenant is disabled
		tenant = tenantRepository.findOne("abc");
		assertEquals("abc", tenant.getId());
		assertEquals("def", tenant.getName());
		assertEquals(TenantStatus.DISABLED, tenant.getStatus());
	}
	
	@Test
	public void testReceiveEnableTenantEvent() throws JMSException {
		// Create new tenant
		Tenant tenant = new Tenant("abc", "def");
		tenant.setStatus(TenantStatus.DISABLED);
		tenantRepository.save(tenant);
		assertNotNull(tenantRepository.findOne("abc"));
		
		// Send message
		Map<String, Object> message = new HashMap<>();
		message.put("tenantId", "abc");
		message.put("action", "enable");
		tenantService.receiveTenantEvents(message);
		
		// Check if tenant is disabled
		tenant = tenantRepository.findOne("abc");
		assertEquals("abc", tenant.getId());
		assertEquals("def", tenant.getName());
		assertEquals(TenantStatus.ENABLED, tenant.getStatus());
	}
	
}
