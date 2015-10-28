package com.predictry.oasis.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * This class contain methods that are designed for use by Thymeleaf views.
 * 
 * @author jocki
 *
 */
public class ViewHelper {

	public String sortParam(Sort sort) {
		if ((sort != null) && (sort.iterator().hasNext())) {
			Order order = sort.iterator().next();
			return order.getProperty() + "," + order.getDirection();
		}
		return null;
	}
	
}
