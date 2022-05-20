package org.macula.plugin.dataset.domain;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.macula.cloud.api.context.CloudApplicationContext;
import org.macula.cloud.api.exception.MaculaCloudException;

/**
 * <p> <b>DataSourceFactory</b> 是创建数据源工厂类. </p>
 */
public class DataSourceFactory {

	static final Map<DataSourceType, DataSourceCreator<?>> creators = new ConcurrentHashMap<DataSourceType, DataSourceCreator<?>>();

	public static final void regist(DataSourceType type, DataSourceCreator<?> creator) {
		creators.put(type, creator);
	}

	public static final DataSourceCreator<?> getCreator(DataSourceType type) {
		return creators.get(type);
	}

	public static final <T> T createTargetDataSource(org.macula.plugin.dataset.domain.DataSource ds) {
		if (ds != null) {
			@SuppressWarnings("unchecked")
			DataSourceCreator<T> creator = (DataSourceCreator<T>) creators.get(ds.getDataSourceType());
			if (creator != null) {
				return creator.createTargetDataSource(ds);
			}
		}
		return null;
	}

	static {
		regist(DataSourceType.DATABASE, new DataSourceCreator<DataSource>() {
			@Override
			public DataSource createTargetDataSource(org.macula.plugin.dataset.domain.DataSource ds) {
				if (ds.isJndi()) {
					try {
						Context initCtx = new InitialContext();
						return (DataSource) initCtx.lookup(ds.getUrl());
					} catch (NamingException e) {
						throw new MaculaCloudException("macula.cloud.data.datasource.namingexception", e);
					}
				}
				DruidDataSource bds = new DruidDataSource();
				bds.setDriverClassName(ds.getDriver());
				bds.setUrl(ds.getUrl());
				bds.setUsername(ds.getUsername());
				bds.setPassword(ds.getPassword());
				if (ds.getMaxActive() > 0) {
					bds.setMaxActive(ds.getMaxActive());
				}

				if (ds.getMaxWait() > 0) {
					bds.setMaxWait(ds.getMaxWait());
				}
				if (ds.getValidationQuery() != null) {
					bds.setValidationQuery(ds.getValidationQuery());
				}
				bds.setInitialSize(1);
				/* bds.setDefaultReadOnly(true); */
				bds.setLogAbandoned(true);
				bds.setRemoveAbandoned(true);
				return bds;
			}
		});
		regist(DataSourceType.LDAP, new DataSourceCreator<InitialDirContext>() {
			@Override
			public InitialDirContext createTargetDataSource(org.macula.plugin.dataset.domain.DataSource ds) {
				if (ds.isJndi()) {
					try {
						Context initCtx = new InitialContext();
						return (InitialDirContext) initCtx.lookup(ds.getUrl());
					} catch (NamingException e) {
						throw new MaculaCloudException("macula.base.data.datasource.namingexception", e);
					}
				}
				try {
					Hashtable<String, String> env = new Hashtable<String, String>();
					env.put(Context.PROVIDER_URL, ds.getUrl());
					env.put(Context.SECURITY_PRINCIPAL, ds.getUsername());
					env.put(Context.SECURITY_CREDENTIALS, ds.getPassword());
					env.put(Context.SECURITY_AUTHENTICATION, "simple");
					env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
					return new InitialDirContext(env);
				} catch (NamingException e) {
					throw new MaculaCloudException("macula.base.data.datasource.namingexception", e);
				}
			}
		});
		regist(DataSourceType.BEAN, new DataSourceCreator<DataSource>() {
			@Override
			public DataSource createTargetDataSource(org.macula.plugin.dataset.domain.DataSource ds) {
				// 从Spring Bean中获取dataSource
				return CloudApplicationContext.getBean(ds.getUrl());
			}
		});
	}
}
