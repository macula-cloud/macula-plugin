package org.macula.plugin.registry.configure;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RegistryAutoConfiguration {

	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [Registry  Plugin] Auto Configure.");
	}
}
