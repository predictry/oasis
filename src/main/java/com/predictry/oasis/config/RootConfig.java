package com.predictry.oasis.config;

import com.predictry.oasis.util.StringTemplateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.script.ScriptEngineManager;
import javax.sql.DataSource;
import java.io.FileNotFoundException;

/**
 * Spring root configuration.
 * 
 * @author jocki
 *
 */
@Configuration
@EnableTransactionManagement
@Import({SchedulerConfig.class, JmsConfig.class})
public class RootConfig {
	
	public static final int TIMEOUT = 60000;
	
	@Autowired
	Environment env;

	@Bean
	public DataSource dataSource() throws NamingException {
		JndiTemplate jndiTemplate = new JndiTemplate();
		return (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/oasis");
	}
	
	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectionRequestTimeout(TIMEOUT);
        factory.setReadTimeout(TIMEOUT);
        factory.setConnectTimeout(TIMEOUT);
		return new RestTemplate(factory);
	}

	@Bean(name = "stringTemplateEngine")
	public SpringTemplateEngine emailTemplateEngine() {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.addTemplateResolver(new StringTemplateResolver());
		return templateEngine;
	}
	
	@Bean
	public ScriptEngineManager scriptEngineManager() {
		return new ScriptEngineManager();
	}
	
	/**
	 * Select log4j configuration file based on active Spring's profile.
	 * 
	 * @throws FileNotFoundException if logger configuration is not found.
	 */
	@PostConstruct
	public void initLog4j() throws FileNotFoundException {
		if (env.acceptsProfiles("dev", "test")) {
			Log4jConfigurer.initLogging("classpath:log4j-development.xml");
		} else {
			Log4jConfigurer.initLogging("classpath:log4j-production.xml");
		}
	}
	
}
