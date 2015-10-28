package com.predictry.oasis.controller;

import java.io.IOException;
import java.util.Map;

import javax.script.ScriptEngineManager;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.predictry.oasis.domain.Application;
import com.predictry.oasis.domain.Task;
import com.predictry.oasis.repository.ServiceProviderRepository;
import com.predictry.oasis.repository.TenantRepository;
import com.predictry.oasis.service.ApplicationService;

/**
 * CRUD controller for <code>Application</code>.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/app")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@Autowired
	private TenantRepository tenantRepository;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ScriptEngineManager scriptEngineManager;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) throws SchedulerException {
		model.addAttribute("apps", applicationService.list());
		return "app/list";
	}
	
	@RequestMapping("/entry")
	public String edit(Model model) {
		model.addAttribute("app", new Application());
		model.addAttribute("serviceProviders", serviceProviderRepository.findAll());
		model.addAttribute("tenants", tenantRepository.findAll());
		return "app/entry";
	}
	
	@RequestMapping("/entry/{id}")
	public String edit(Model model, @PathVariable("id") Application app) {
		model.addAttribute("app", (app == null ? new Application() : app));
		model.addAttribute("serviceProviders", serviceProviderRepository.findAll());
		model.addAttribute("tenants", tenantRepository.findAll());
		return "app/entry";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute Application app) throws SchedulerException {
		applicationService.add(app);
		return "redirect:/app";
	}
	
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable("id") Application app) throws SchedulerException {
		applicationService.delete(app);
		return "redirect:/app";
	}
	
	@RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
	public String detail(Model model, @PathVariable("id") Application app) {
		model.addAttribute("app", app);
		return "app/detail/list";
	}
	
	@RequestMapping(value = "/{id}/detail/entry")
	public String detailEdit(Model model, @PathVariable("id") Application app) {
		model.addAttribute("app", app);
		model.addAttribute("task", new Task());
		return "app/detail/entry";
	}
	
	@RequestMapping(value = "/{id}/detail/entry/{index}")
	public String detailEdit(Model model, @PathVariable("id") Application app, @PathVariable("index") Integer index) {
		model.addAttribute("app", app);
		model.addAttribute("task", app.getTask(index));
		model.addAttribute("index", index);
		return "app/detail/entry";
	}
	
	@RequestMapping(value = "/{id}/detail/save")
	public String detailSave(@ModelAttribute Task task, @PathVariable("id") Application app, Model model) throws JsonParseException, JsonMappingException, IOException {
		// Validate Json
		objectMapper.readValue(task.evaluatePayload(scriptEngineManager), Map.class);
		
		applicationService.addTask(app, task);
		return "redirect:/app/" + app.getId() + "/detail";
	}
	
	@RequestMapping(value = "/{id}/detail/save/{index}")
	public String detailSave(@ModelAttribute Task task, @PathVariable("id") Application app, @PathVariable("index") Integer index) throws JsonParseException, JsonMappingException, IOException {
		// Validate Json
		objectMapper.readValue(task.evaluatePayload(scriptEngineManager), Map.class);
		
		app = applicationService.editTask(app, task, index);
		return "redirect:/app/" + app.getId() + "/detail";
	}
	
	@RequestMapping(value = "/{id}/detail/delete/{index}")
	public String detailDelete(Model model, @PathVariable("id") Application app, @PathVariable("index") Integer index) {
		app = applicationService.deleteTask(app, index);
		model.addAttribute("app", app);
		return "app/detail/list";
	}
	
}
