package org.macula.plugin.datalog.configure;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.datalog.event.ChangedContext;
import org.macula.plugin.datalog.event.ChangedContextProvider;
import org.macula.plugin.datalog.event.ChangedEvent;
import org.macula.plugin.datalog.interceptor.MappedAuditInterceptor;
import org.macula.plugin.datalog.meta.DatabaseMetadataReader;
import org.macula.plugin.datalog.meta.MetadataReader;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AutoConfigureAfter({
		SqlInitializationAutoConfiguration.class })
public class MappedAuditAutoConfiguration {
	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [Datalog] Auto Configure.");
	}

	@Bean
	public MappedAuditInterceptor mybatisAuditlogInterceptor(ChangedContextProvider changedContextProvider, MetadataReader metadataReader) {
		MappedAuditInterceptor interceptor = new MappedAuditInterceptor();
		interceptor.setChangedContextProvider(changedContextProvider);
		interceptor.setMetadataReader(metadataReader);

		log.debug("[Macula] |- Bean [MappedAuditInterceptor] Auto Configure.");
		return interceptor;
	}

	@Bean
	@ConditionalOnMissingBean(MetadataReader.class)
	public MetadataReader DatabaseMetadataReader(DataSource dataSource,
			/* 需要在执行SQL初始化脚本后，不能移除这个依赖 */
			SqlDataSourceScriptDatabaseInitializer db) throws SQLException {
		log.debug("[Macula] |- Bean [DatabaseMetadataReader] Auto Configure.");
		DatabaseMetadataReader metadataReader = new DatabaseMetadataReader(dataSource);
		metadataReader.afterPropertiesSet();
		return metadataReader;
	}

	@Bean
	@ConditionalOnMissingBean(ChangedContextProvider.class)
	public ChangedContextProvider createEventContextProvider() {
		log.debug("[Macula] |- Bean [ChangedContextProvider] Auto Configure.");
		return new ChangedContextProvider() {

			@Override
			public ChangedContext getEventContext() {
				return new ChangedContext();
			}

			@Override
			public List<Consumer<ChangedEvent>> getConsumers() {
				return Arrays.asList((event) -> log.info("===== Event: [{}]", event));
			}
		};
	}
}
