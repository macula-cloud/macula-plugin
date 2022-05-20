package org.macula.cloud.mq.configure;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "macula.cloud.alimq")
public class AliMQConfig {

	private Map<String, String> props;

	public Properties getProperties() {
		Properties properties = new Properties();
		properties.putAll(props);
		return properties;
	}
}
