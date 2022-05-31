package org.macula.plugins.flow.config;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.bstek.uflo.console.UfloServlet;

@Configuration
public class UfloSpringBeansConfigurer {

	@Bean("ufloSessionFactory")
	public LocalSessionFactoryBean localSessionFactoryBean(DataSource dataSource)
			throws PropertyVetoException, IOException {
		LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setPackagesToScan("com.bstek.uflo.model");
		Properties prop = new Properties();
		prop.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		prop.put("hibernate.show_sql", true);
		prop.put("hibernate.hbm2ddl.auto", "update");
		prop.put("hibernate.jdbc.batch_size", 100);
		sessionFactoryBean.setHibernateProperties(prop);

		return sessionFactoryBean;
	}

	@Bean("ufloTransactionManager")
	public HibernateTransactionManager ufloTransactionManager(
			@Qualifier("ufloSessionFactory") SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory);
		return transactionManager;
	}

	@Bean
	public ServletRegistrationBean<UfloServlet> servletRegistration() {
		return new ServletRegistrationBean<UfloServlet>(new UfloServlet(), "/uflo/*");
	}

}
