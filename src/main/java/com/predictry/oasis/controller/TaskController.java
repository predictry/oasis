package com.predictry.oasis.controller;

import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.predictry.oasis.domain.Task;
import com.predictry.oasis.repository.ServiceProviderRepository;
import com.predictry.oasis.service.TaskService;

/**
 * CRUD controller for Task.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/task")
public class TaskController {

	@Autowired
	private TaskService taskService;
	
	@Autowired
	private ServiceProviderRepository serviceProviderRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) throws SchedulerException {
		model.addAttribute("tasks", taskService.list());
		return "task/list";
	}
	
	@RequestMapping("/entry")
	public String edit(Model model) {
		model.addAttribute("task", new Task());
		model.addAttribute("serviceProviders", serviceProviderRepository.findAll());
		return "task/entry";
	}
	
	@RequestMapping("/entry/{id}")
	public String edit(Model model, @PathVariable Long id) {
		Task task = taskService.findById(id);
		model.addAttribute("task", (task == null ? new Task() : task));
		model.addAttribute("serviceProviders", serviceProviderRepository.findAll());
		return "task/entry";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@ModelAttribute Task task) throws SchedulerException {
		taskService.add(task);
		return "redirect:/task";
	}
	
	@RequestMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id) throws SchedulerException {
		taskService.delete(id);
		return "redirect:/task";
	}
	
}
