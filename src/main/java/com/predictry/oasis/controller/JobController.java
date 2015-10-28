package com.predictry.oasis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.predictry.oasis.domain.Job;
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
	public String list(Model model, Pageable pageable) {
		Page<Job> page = jobRepository.findAll(pageable);
		model.addAttribute("jobs", page.getContent());
		model.addAttribute("page", page);
		return "job/list";
	}
	
	@RequestMapping(value = "/{id}/popup", method = RequestMethod.GET)
	public String popup(Model model, @PathVariable("id") Job job) {
		model.addAttribute("job", job);
		return "job/fragment :: detail_popup";
	}
	
}
