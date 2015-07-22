package com.predictry.oasis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Exception handler for global error if the error is not handled in
 * respective controller.
 * 
 * @author jocki
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * General error handler controller.
	 */
	@ExceptionHandler(value = {Exception.class, RuntimeException.class})
	public ModelAndView error(Exception ex) {
		LOG.error("Handling global exception", ex);
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("exception", ex);
		return mav;
	}
	
}
