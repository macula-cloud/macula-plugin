package org.macula.plugin.execlog.configure;

import org.macula.plugin.execlog.interceptor.AnnotationServiceInvokeAspect;
import org.macula.plugin.execlog.interceptor.ServiceInvokeLogService;
import org.macula.plugin.execlog.repository.ServiceInvokeLogRepository;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigureOrder(AutoConfigureOrder.DEFAULT_ORDER + 100)
@EnableAspectJAutoProxy
@ConditionalOnProperty(prefix = "macula.cloud.invokelog", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaRepositories(basePackages = "org.macula.plugin.execlog.repository")
@EntityScan(basePackages = "org.macula.plugin.execlog.domain")
public class ServiceInvokeProxyAutoConfiguration {

	@Bean
	public ServiceInvokeLogService serviceInvokeLogService(final ServiceInvokeLogRepository repository) {
		return new ServiceInvokeLogService(repository);
	}

	@Bean
	public AnnotationServiceInvokeAspect AnnotationServiceInvokeAspect(ServiceInvokeLogService serviceInvokeLogService) {
		return new AnnotationServiceInvokeAspect(serviceInvokeLogService);
	}
}
