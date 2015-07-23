package com.predictry.oasis.controller;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for scheduler related operations.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/scheduler")
public class SchedulerController {
	
	@Autowired
	private Scheduler scheduler;

	@RequestMapping(method = RequestMethod.GET)
	public String scheduler(ModelMap model) throws SchedulerException {
		SchedulerMetaData metadata = scheduler.getMetaData();
		model.addAttribute("runningSince", metadata.getRunningSince());
		model.addAttribute("isStandby", metadata.isInStandbyMode());
		model.addAttribute("numOfJobsExecuted", metadata.getNumberOfJobsExecuted());
		model.addAttribute("threadPoolClass", metadata.getThreadPoolClass().getName());
		model.addAttribute("threadPoolSize", metadata.getThreadPoolSize());
		return "scheduler/status";
	}
	
	@RequestMapping("/start")
	public String start() throws SchedulerException {
		if (scheduler.isInStandbyMode()) {
			scheduler.start();
		}
		return "redirect:/scheduler";
	}
	
	@RequestMapping("/standby")
	public String standby() throws SchedulerException {
		if (!scheduler.isInStandbyMode()) {
			scheduler.standby();
		}
		return "redirect:/scheduler";
	}
	
	@RequestMapping("/heartbeat")
	public @ResponseBody String test() {
		return "{ \"ok\": true }";
	}
}
