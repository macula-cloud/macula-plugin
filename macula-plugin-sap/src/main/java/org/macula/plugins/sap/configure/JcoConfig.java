package org.macula.plugin.sap.configure;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "macula.plugin.sap")
public class JcoConfig {

	private String name;
	private List<String> packages;
	private Map<String, String> props;

}
