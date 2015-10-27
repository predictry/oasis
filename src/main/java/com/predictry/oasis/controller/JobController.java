package com.predictry.oasis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.predictry.oasis.repository.JobRepository;

/**
 * Controller for <code>Job</code>.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/job")
public class JobController {

	@Autowired
	private JobRepository jobRepository;
	
	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model) {
		model.addAttribute("jobs", jobRepository.findAll());
		return "job/list";
	}
	
}
