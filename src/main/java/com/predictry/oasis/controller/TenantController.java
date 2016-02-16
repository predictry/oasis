package com.predictry.oasis.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.predictry.oasis.domain.Tenant;
import com.predictry.oasis.repository.TenantRepository;

/**
 * CRUD controller for <code>Tenant</code>.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/tenant")
public class TenantController {

	@Autowired
	private TenantRepository tenantRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model, Pageable pageable) throws SchedulerException {
		Page<Tenant> page = tenantRepository.findAll(pageable);
		model.addAttribute("tenants", page.getContent());
		model.addAttribute("page", page);
		return "tenant/list";
	}
	
	@RequestMapping("/entry")
	public String edit(Model model) {
		model.addAttribute("tenant", new Tenant());
		return "tenant/entry";
	}
	
	@RequestMapping("/entry/{id}")
	public String edit(Model model, @PathVariable("id") Tenant tenant) {
		model.addAttribute("tenant", (tenant == null ? new Tenant() : tenant));
		return "tenant/entry";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute Tenant tenant) throws SchedulerException {
		tenantRepository.saveAndFlush(tenant);
		return "redirect:/tenant";
	}

	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable("id") Tenant tenant) throws SchedulerException {
		tenantRepository.delete(tenant);
		return "redirect:/tenant";
	}
	
}
