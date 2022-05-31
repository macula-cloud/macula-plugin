package org.macula.plugins.alimq.configure;

import java.util.List;

import org.macula.plugins.alimq.AliMQMessageListener;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;

@Configuration
@EnableConfigurationProperties(AliMQConfig.class)
public class AliMQAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	private Producer producer;
	private Consumer consumer;

	@Bean(destroyMethod = "shutdown")
	public Producer producer(AliMQConfig config) {
		this.producer = ONSFactory.createProducer(config.getProperties());
		return this.producer;
	}

	@Bean(destroyMethod = "shutdown")
	public Consumer getOrderConsumer(AliMQConfig config, List<AliMQMessageListener> listeners) {
		this.consumer = ONSFactory.createConsumer(config.getProperties());
		listeners.forEach(listener -> {
			String topic = listener.getTopic();
			List<String> expressions = listener.getSubExpression();
			if (topic != null && !CollectionUtils.isEmpty(expressions)) {
				this.consumer.subscribe(topic, StringUtils.collectionToDelimitedString(expressions, "||"), listener);
			}
		});
		return this.consumer;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getSource() instanceof AnnotationConfigServletWebServerApplicationContext) {
			if (this.producer != null && !this.producer.isStarted()) {
				this.producer.start();
			}
			if (this.consumer != null && !this.consumer.isStarted()) {
				this.consumer.start();
			}
		}
	}

}
