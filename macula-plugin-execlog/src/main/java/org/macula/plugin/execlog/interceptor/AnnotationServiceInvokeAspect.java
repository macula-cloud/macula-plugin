package org.macula.plugin.execlog.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.macula.engine.assistant.support.ApplicationId;
import org.macula.engine.commons.utils.SystemUtils;
import org.macula.plugin.execlog.annotation.ServiceInvokeProxy;
import org.macula.plugin.execlog.domain.ServiceInvokeLog;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;

@Aspect
@Slf4j
public class AnnotationServiceInvokeAspect {

	private final ServiceInvokeLogService serviceInvokeLogService;

	public AnnotationServiceInvokeAspect(ServiceInvokeLogService serviceInvokeLogService) {
		this.serviceInvokeLogService = serviceInvokeLogService;
	}

	@Around("@annotation(serviceInvokeProxy)")
	public Object around(ProceedingJoinPoint joinPoint, ServiceInvokeProxy serviceInvokeProxy) throws Throwable {
		ServiceInvokeLog serviceInvokeLog = new ServiceInvokeLog();
		serviceInvokeLog.setTransactionId(Thread.currentThread().getName() + "@" + ApplicationId.current().getApplicationKey());
		ServiceInvokeRootObject rootObject = createInvokeContext(joinPoint);
		before(serviceInvokeProxy, rootObject, serviceInvokeLog);
		Object result = null;
		Exception exception = null;
		try {
			result = joinPoint.proceed();
			return result;
		} catch (Exception ex) {
			exception = ex;
			throw ex;
		} finally {
			rootObject.setResult(result);
			rootObject.setE(exception);
			after(serviceInvokeProxy, rootObject, serviceInvokeLog);
		}
	}

	protected ServiceInvokeRootObject createInvokeContext(ProceedingJoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		method = BridgeMethodResolver.findBridgedMethod(method);
		Object source = joinPoint.getTarget();
		Class<?> sourceClass = method.getDeclaringClass();
		Object[] args = joinPoint.getArgs();
		Class<?> targetClass = getTargetClass(source);
		Method targetMethod = (!Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : method);
		return new ServiceInvokeRootObject(method, args, source, sourceClass, targetClass, targetMethod);
	}

	protected void before(ServiceInvokeProxy serviceInvokeProxy, ServiceInvokeRootObject rootObject, ServiceInvokeLog serviceInvokeLog) {
		try {
			serviceInvokeLog.setSourceTimestamp(SystemUtils.getCurrentTime());
			serviceInvokeLogService.processServiceInvokeLog(serviceInvokeProxy, rootObject, serviceInvokeLog, true);
		} catch (Exception ex) {
			log.debug("AnnotationServiceInvokeAspect.before error:", ex);
		}
	}

	protected void after(ServiceInvokeProxy serviceInvokeProxy, ServiceInvokeRootObject rootObject, ServiceInvokeLog serviceInvokeLog) {
		try {
			log.info("AnnotationServiceInvokeAspect.after");
			serviceInvokeLog.setTargetTimestamp(SystemUtils.getCurrentTime());
			serviceInvokeLogService.processServiceInvokeLog(serviceInvokeProxy, rootObject, serviceInvokeLog, false);
		} catch (Exception ex) {
			log.error("AnnotationServiceInvokeAspect.after error:", ex);
		}
	}

	private Class<?> getTargetClass(Object target) {
		return AopProxyUtils.ultimateTargetClass(target);
	}

}
