package org.macula.plugin.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource(locations = {
		"classpath:uflo-console-context.xml" })
@SpringBootApplication
public class FlowPluginApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowPluginApplication.class, args);
	}
}
