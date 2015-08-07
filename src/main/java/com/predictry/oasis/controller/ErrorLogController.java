package com.predictry.oasis.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * CRUD controller for <code>ErrorLog</code>.
 * 
 * @author jocki
 *
 */
@Controller
@RequestMapping("/errorLog")
public class ErrorLogController {

	@RequestMapping(method = RequestMethod.GET)
	public String list(Model model, Pageable pageable) {
		return "errorLog/list";
	}
}
