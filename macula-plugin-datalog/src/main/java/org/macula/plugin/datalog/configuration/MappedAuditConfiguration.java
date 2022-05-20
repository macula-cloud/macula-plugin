package org.macula.plugin.datalog.configuration;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.datalog.event.ChangedEvent;
import org.macula.plugin.datalog.event.EventContext;
import org.macula.plugin.datalog.event.EventContextAware;
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
public class MappedAuditConfiguration {
	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [Datalog] Auto Configure.");
	}

	@Bean
	public MappedAuditInterceptor mybatisAuditlogInterceptor(EventContextAware eventContextAware, MetadataReader metadataReader) {
		MappedAuditInterceptor interceptor = new MappedAuditInterceptor();
		interceptor.setEventContextAware(eventContextAware);
		interceptor.setMetadataReader(metadataReader);

		log.debug("[Macula] |- Bean [MappedAuditInterceptor] Auto Configure.");
		return interceptor;
	}

	@Bean
	@ConditionalOnMissingBean(MetadataReader.class)
	public DatabaseMetadataReader DatabaseMetadataReader(DataSource dataSource,
			/* 需要在执行SQL初始化脚本后，不能移除这个依赖 */
			SqlDataSourceScriptDatabaseInitializer db) throws SQLException {
		log.debug("[Macula] |- Bean [DatabaseMetadataReader] Auto Configure.");
		DatabaseMetadataReader metadataReader = new DatabaseMetadataReader(dataSource);
		metadataReader.afterPropertiesSet();
		return metadataReader;
	}

	@Bean
	@ConditionalOnMissingBean(EventContextAware.class)
	public EventContextAware createEventContextAware() {
		log.debug("[Macula] |- Bean [EventContextAware] Auto Configure.");
		return new EventContextAware() {

			@Override
			public EventContext getEventContext() {
				return new EventContext();
			}

			@Override
			public void processEvent(EventContext context) {
				if (context != null) {
					List<ChangedEvent> events = context.getChangedEvents();
					if (events != null) {
						System.out.println("===== Events:" + events);
					}
				}
			}
		};
	}
}
