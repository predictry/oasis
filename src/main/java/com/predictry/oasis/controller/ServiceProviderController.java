package com.predictry.oasis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.repository.ServiceProviderRepository;
import com.predictry.oasis.service.HeartbeatService;

/**
 * CRUD controller for ServiceProvider.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/serviceProvider")
public class ServiceProviderController {

	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private HeartbeatService heartbeatService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("serviceProviders", serviceProviderRepository.findAll());
		return "serviceProvider/list";
	}
	
	@RequestMapping("/entry")
	public String edit(Model model) {
		model.addAttribute("serviceProvider", new ServiceProvider());
		return "serviceProvider/entry";
	}
	
	@RequestMapping("/entry/{id}")
	public String edit(Model model, @PathVariable Long id) {
		ServiceProvider serviceProvider = serviceProviderRepository.findOne(id);
		model.addAttribute("serviceProvider", 
			(serviceProvider == null ? new ServiceProvider() : serviceProvider));
		return "serviceProvider/entry";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute ServiceProvider serviceProvider) {
		serviceProviderRepository.save(serviceProvider);
		return "redirect:/serviceProvider";
	}
	
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id) {
		serviceProviderRepository.delete(id);
		return "redirect:/serviceProvider";
	}
	
	@RequestMapping(value = "/ping/{id}")
	public String ping(@PathVariable Long id) {
		heartbeatService.ping(id);
		return "redirect:/serviceProvider";
	}
	
}
