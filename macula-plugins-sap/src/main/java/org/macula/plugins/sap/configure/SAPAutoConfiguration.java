package org.macula.plugins.sap.configure;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibersap.annotations.Bapi;
import org.hibersap.configuration.AnnotationConfiguration;
import org.hibersap.configuration.xml.SessionManagerConfig;
import org.hibersap.execution.jco.JCoContext;
import org.hibersap.session.SessionManager;
import org.macula.plugins.sap.SAPExecution;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

@Slf4j
@Configuration
@EnableConfigurationProperties(JcoConfig.class)
public class SAPAutoConfiguration {

	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [SAP Plugin] Auto Configure.");
	}

	@Bean
	public SessionManagerConfig createSessionManagerConfig(JcoConfig config) {
		SessionManagerConfig sessionManagerConfig = new SessionManagerConfig(config.getName()).setContext(JCoContext.class.getName());
		for (Entry<String, String> entry : config.getProps().entrySet()) {
			String key = entry.getKey().toLowerCase();
			String value = entry.getValue();
			if (!key.contains("-")) {
				log.trace("[Macula] |- [SAP SessionManagerConfig] Set property {} -> {} ", key, value);
				sessionManagerConfig.setProperty(entry.getKey(), value);
			}
		}
		for (Entry<String, String> entry : config.getProps().entrySet()) {
			String key = entry.getKey().toLowerCase();
			String value = entry.getValue();
			if (key.contains("-")) {
				key = StringUtils.replaceChars(entry.getKey(), '-', '.');
				log.trace("[Macula] |- [SAP SessionManagerConfig] Set property {} -> {} ", key, value);
				sessionManagerConfig.setProperty(entry.getKey(), value);
			}
		}

		Map<String, String> bapis = getBapiMap(config);
		for (Map.Entry<String, String> entry : bapis.entrySet()) {
			String className = entry.getValue();
			sessionManagerConfig.addAnnotatedClass(className);
		}

		log.trace("[Macula] |- Bean [SAP SessionManagerConfig] Auto Configure.");
		return sessionManagerConfig;
	}

	@Bean
	public SessionManager getSessionManager(SessionManagerConfig config) {
		AnnotationConfiguration configuration = new AnnotationConfiguration(config);

		log.trace("[Macula] |- Bean [SAP SessionManager] Auto Configure.");
		return configuration.buildSessionManager();
	}

	@Bean
	public SAPExecution getSAPExecution(SessionManager sessionManager) {
		log.trace("[Macula] |- Bean [SAP SAPExecution] Auto Configure.");
		return new SAPExecution(sessionManager);
	}

	public Map<String, String> getBapiMap(JcoConfig config) {
		final Map<String, String> result = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		List<String> packages = config.getPackages();
		if (packages != null) {
			ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
			provider.addIncludeFilter(new AnnotationTypeFilter(Bapi.class));
			packages.forEach(basePackage -> {
				Set<BeanDefinition> components = provider.findCandidateComponents(basePackage);
				components.forEach(component -> {
					String className = component.getBeanClassName();
					Class<?> clazz;
					try {
						clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
					} catch (ClassNotFoundException e) {
						return;
					} catch (LinkageError e) {
						return;
					}
					if (clazz != null) {
						Bapi annotation = AnnotationUtils.findAnnotation(clazz, Bapi.class);
						if (annotation != null) {
							result.put(annotation.value(), className);
						}
					}
				});
			});
		}
		return result;
	}

}
