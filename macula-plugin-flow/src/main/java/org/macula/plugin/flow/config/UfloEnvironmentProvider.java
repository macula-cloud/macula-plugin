package org.macula.plugin.flow.config;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.bstek.uflo.env.EnvironmentProvider;

@Component
public class UfloEnvironmentProvider implements EnvironmentProvider {

	@Resource(name = "ufloSessionFactory")
	private SessionFactory sessionFactory;

	@Resource(name = "ufloTransactionManager")
	private PlatformTransactionManager platformTransactionManager;

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public PlatformTransactionManager getPlatformTransactionManager() {
		return platformTransactionManager;
	}

	@Override
	public String getLoginUser() {
		return "anonymous";
	}

	@Override
	public String getCategoryId() {
		return "anonymous";
	}

}
