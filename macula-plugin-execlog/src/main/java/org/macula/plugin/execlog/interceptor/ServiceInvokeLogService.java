package org.macula.plugin.execlog.interceptor;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.macula.engine.assistant.support.ApplicationId;
import org.macula.plugin.execlog.annotation.ServiceInvokeProxy;
import org.macula.plugin.execlog.domain.ServiceInvokeLog;
import org.macula.plugin.execlog.event.ServiceInvokeAlarmEvent;
import org.macula.plugin.execlog.repository.ServiceInvokeLogRepository;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
public class ServiceInvokeLogService implements ApplicationContextAware, BeanFactoryAware {

	private final ExecutorService executors = Executors.newFixedThreadPool(2);
	private final ServiceInvokeLogRepository repository;
	private BeanFactory beanFactory;
	private ApplicationContext applicationContext;

	public ServiceInvokeLogService(ServiceInvokeLogRepository repository) {
		this.repository = repository;
	}

	public void processServiceInvokeLog(ServiceInvokeProxy serviceInvokeProxy, ServiceInvokeRootObject rootObject, ServiceInvokeLog serviceInvokeLog,
			boolean before) throws Exception {
		executors.execute(new SaveInvokeLogRunnable(serviceInvokeProxy, rootObject, serviceInvokeLog, before));
	}

	@Transactional
	public void saveServiceInvokeLog(org.macula.plugin.execlog.domain.ServiceInvokeLog entity) {
		repository.saveAndFlush(entity);
	}

	private class SaveInvokeLogRunnable implements Runnable {
		private final ServiceInvokeLog invokeLog;
		private final ServiceInvokeProxy serviceInvokeProxy;
		private final ServiceInvokeRootObject rootObject;
		private final boolean before;

		private SaveInvokeLogRunnable(ServiceInvokeProxy serviceInvokeProxy, ServiceInvokeRootObject rootObject, ServiceInvokeLog invokeLog,
				boolean before) {
			this.serviceInvokeProxy = serviceInvokeProxy;
			this.rootObject = rootObject;
			this.invokeLog = invokeLog;
			this.before = before;
		}

		@Override
		public void run() {
			try {
				synchronized (invokeLog) {
					ServiceInvokeExpressionEvaluator expressionEvaluator = new ServiceInvokeExpressionEvaluator(rootObject, beanFactory);

					if (before) {
						invokeLog.setDataKey(expressionEvaluator.getStringExpression(serviceInvokeProxy.key()));
						invokeLog.setSourceMethod(expressionEvaluator.getStringExpression(serviceInvokeProxy.description()));
						invokeLog.setNode(ApplicationId.current().getInstanceKey());
						invokeLog.setSource(expressionEvaluator.getStringExpression(serviceInvokeProxy.source()));
						invokeLog.setSourceMethod(expressionEvaluator.getStringExpression(serviceInvokeProxy.sourceMethod()));
						invokeLog.setSourceMessage(expressionEvaluator.getStringExpression(serviceInvokeProxy.sourceMessage()));
						invokeLog.setTarget(expressionEvaluator.getStringExpression(serviceInvokeProxy.target()));
						invokeLog.setTargetMethod(expressionEvaluator.getStringExpression(serviceInvokeProxy.targetMethod()));
					} else {
						invokeLog.setTargetMessage(expressionEvaluator.getStringExpression(serviceInvokeProxy.targetMessage()));
						invokeLog.setExceptionMessage(expressionEvaluator.getStringExpression(serviceInvokeProxy.exceptionMessage()));
						invokeLog.setStatus(expressionEvaluator.getBooleanExpression(serviceInvokeProxy.success()) ? "SUCCESS" : "ERROR");
					}

					org.macula.plugin.execlog.domain.ServiceInvokeLog entity = null;
					if (invokeLog.getId() != null) {
						Optional<org.macula.plugin.execlog.domain.ServiceInvokeLog> optional = repository.findById(invokeLog.getId());
						if (optional.isPresent()) {
							entity = optional.get();
						}
					}
					if (entity == null) {
						entity = new org.macula.plugin.execlog.domain.ServiceInvokeLog();
					}
					invokeLog.clone(entity);
					saveServiceInvokeLog(entity);
					entity.cloneId(invokeLog);
				}

				if (serviceInvokeProxy.alarm() && invokeLog.getExceptionMessage() != null) {
					applicationContext.publishEvent(new ServiceInvokeAlarmEvent(invokeLog));
				}
			} catch (

			Exception ex) {
				log.error("Save ServiceInvokeLog error : ", ex);
			}
		}

	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
