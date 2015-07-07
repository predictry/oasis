package com.predictry.oasis.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample REST controller.
 * 
 * @author jocki
 *
 */
@RestController
public class TestController {

	/**
	 * Sample test method.
	 * @return "Hello World!"
	 */
	@RequestMapping("/test")
	public String test() {
		return "Hello World!";
	}
	
}
