package org.macula.plugin.execlog.configure;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.execlog.event.ExecutionContext;
import org.macula.plugin.execlog.event.ExecutionContextProvider;
import org.macula.plugin.execlog.event.ExecutionEvent;
import org.macula.plugin.execlog.interceptor.ExecutionLogAspect;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
public class ExecutionLogAutoConfiguration {

	@PostConstruct
	public void postConstruct() {
		log.debug("[Macula] |- Plugin [Execlog] Auto Configure.");
	}

	@Bean
	public ExecutionLogAspect ExecutionLogAspect(ExecutionContextProvider executionContextProvider) {
		log.debug("[Macula] |- Bean [ExecutionLogAspect] Auto Configure.");
		return new ExecutionLogAspect(executionContextProvider);
	}

	@Bean
	@ConditionalOnMissingBean(ExecutionContextProvider.class)
	public ExecutionContextProvider createExecutionContextProvider() {
		log.debug("[Macula] |- Bean [ExecutionContextProvider] Auto Configure.");
		return new ExecutionContextProvider() {

			@Override
			public ExecutionContext getExecutionContext() {
				return new ExecutionContext();
			}

			@Override
			public List<Consumer<ExecutionEvent>> getConsumers() {
				return Arrays.asList((event) -> log.info("===== Event: [{}]", event));
			}
		};
	}
}
