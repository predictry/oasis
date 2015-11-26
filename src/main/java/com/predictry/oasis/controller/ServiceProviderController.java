package com.predictry.oasis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.predictry.oasis.domain.ServiceProvider;
import com.predictry.oasis.repository.ServiceProviderRepository;
import com.predictry.oasis.service.EC2Service;

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
	private EC2Service ec2Service;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model, Pageable pageable) {
		Page<ServiceProvider> page = serviceProviderRepository.findAll(pageable);
		model.addAttribute("serviceProviders", page.getContent());
		model.addAttribute("page", page);
		return "serviceProvider/list";
	}
	
	@RequestMapping("/entry")
	public String edit(Model model) {
		model.addAttribute("serviceProvider", new ServiceProvider());
		return "serviceProvider/entry";
	}
	
	@RequestMapping("/entry/{id}")
	public String edit(Model model, @PathVariable("id") ServiceProvider serviceProvider) {
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
	public String delete(@PathVariable("id") ServiceProvider serviceProvider) {
		serviceProviderRepository.delete(serviceProvider);
		return "redirect:/serviceProvider";
	}
	
	@RequestMapping(value = "/start/{id}")
	public String start(@PathVariable("id") ServiceProvider serviceProvider) {
		ec2Service.startInstance(serviceProvider);
		return "redirect:/serviceProvider";
	}
	
	@RequestMapping(value = "/stop/{id}")
	public String stop(@PathVariable("id") ServiceProvider serviceProvider) {
		ec2Service.stopInstance(serviceProvider);
		return "redirect:/serviceProvider";
	}
	
	@RequestMapping(value = "/check/{id}")
	public String check(@PathVariable("id") ServiceProvider serviceProvider) {
		ec2Service.checkStatus(serviceProvider);
		return "redirect:/serviceProvider";
	}
	
}
