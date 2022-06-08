package org.macula.plugins.cainiao.configure;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugins.cainiao.CainiaoLinkService;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties({
		CainiaoConfig.class })
public class CainiaoAutoConfiguration {

	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [Cainiao Plugin] Auto Configure.");
	}

	@Bean
	public CainiaoLinkService cainiaoLinkServiceBean(CainiaoConfig config) {
		log.trace("[Macula] |- Bean [Cainiao CainiaoLinkService] Auto Configure.");
		return new CainiaoLinkService(config);
	}
}
