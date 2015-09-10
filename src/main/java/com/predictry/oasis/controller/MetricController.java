package com.predictry.oasis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.predictry.oasis.service.MetricService;

/**
 * This is API for internal usage related to admin metrics collections.
 * 
 * @author jocki
 *
 */
@RestController
@RequestMapping("/metric")
public class MetricController {

	private static final Logger LOG = LoggerFactory.getLogger(MetricController.class);

	@Autowired
	private MetricService metricService;
	
	@RequestMapping("/execute")
	public void execute() {
		LOG.info("Executing metric collection manually.");
		metricService.measureStorage();
		metricService.measureSpeed();
		LOG.info("Metric collection done.");
	}
	
	
}
