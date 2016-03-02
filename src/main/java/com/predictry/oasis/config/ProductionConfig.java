package com.predictry.oasis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring root configuration for production (defined as not dev environment).
 *
 * @author jocki
 */
@Configuration
@Profile("!dev")
public class ProductionConfig {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private DataSource dataSource;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPackagesToScan("com.predictry.oasis.domain");

        Map<String, ? super Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        emf.setJpaPropertyMap(jpaProperties);

        return emf;
    }

}
