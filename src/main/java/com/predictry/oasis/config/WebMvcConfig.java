package com.predictry.oasis.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring WebMVC configuration.
 * 
 * @author jocki
 *
 */
@Configuration
@ComponentScan("com.predictry.oasis")
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

}
