package com.predictry.oasis.config;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Log4jConfigurer;
import org.springframework.web.client.RestTemplate;

/**
 * Spring root configuration.
 * 
 * @author jocki
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.predictry.oasis.repository")
@Import(SchedulerConfig.class)
public class RootConfig {
	
	public static final int TIMEOUT = 500;
	
	@Autowired
	Environment env;
	
	@Bean
	@Profile("!dev")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {		
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setPackagesToScan("com.predictry.oasis.domain");
		
		Map<String, ? super Object> jpaProperties = new HashMap<>();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		emf.setJpaPropertyMap(jpaProperties);
		
		return emf;
	}
	
	@Bean(name = "entityManagerFactory")
	@Profile("dev")
	public LocalContainerEntityManagerFactoryBean devEntityManagerFactory() throws NamingException {		
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		emf.setPackagesToScan("com.predictry.oasis.domain");
		
		Map<String, ? super Object> jpaProperties = new HashMap<>();
		jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		jpaProperties.put("javax.persistence.schema-generation.database.action", "drop-and-create");
		jpaProperties.put("javax.persistence.schema-generation.drop-source", "script-then-metadata");
		jpaProperties.put("javax.persistence.schema-generation.drop-script-source", "META-INF/sql/drop.sql");
		emf.setJpaPropertyMap(jpaProperties);
		
		return emf;
	}
	
	@Bean
	public DataSource dataSource() throws NamingException {
		JndiTemplate jndiTemplate = new JndiTemplate();
		DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/oasis");
		return dataSource;
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
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
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
