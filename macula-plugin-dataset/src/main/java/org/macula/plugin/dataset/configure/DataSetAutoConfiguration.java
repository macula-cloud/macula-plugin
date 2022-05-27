package org.macula.plugin.dataset.configure;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DataSetAutoConfiguration {

	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [DataSet Plugin] Auto Configure.");
	}

}
