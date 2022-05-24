package org.macula.plugin.execlog.event;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.macula.engine.assistant.support.ApplicationId;
import org.macula.engine.assistant.utils.SystemUtils;
import org.macula.plugin.execlog.annotation.Execlog;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.lang.Nullable;

/**
 * <p>Execution Context</p>
 */
@Slf4j
public class ExecutionContext {

	@Getter
	@Setter
	private String transactionId;

	private ExecutionRootObject executionObject;

	private List<ExecutionEvent> executionEvents = new ArrayList<>();

	public ExecutionEvent start() {
		ExecutionExpressionEvaluator expressionEvaluator = new ExecutionExpressionEvaluator(executionObject, SpringUtil.getApplicationContext());
		ExecutionEvent event = new ExecutionEvent();
		event.setTransactionId(transactionId);
		event.setSourceTimestamp(SystemUtils.getCurrentTime());
		event.setDataKey(expressionEvaluator.getStringExpression(executionObject.getExeclog().key()));
		event.setSourceMethod(expressionEvaluator.getStringExpression(executionObject.getExeclog().description()));
		event.setNode(ApplicationId.current().getInstanceKey());
		event.setSource(expressionEvaluator.getStringExpression(executionObject.getExeclog().source()));
		event.setSourceMethod(expressionEvaluator.getStringExpression(executionObject.getExeclog().sourceMethod()));
		event.setSourceMessage(expressionEvaluator.getStringExpression(executionObject.getExeclog().sourceMessage()));
		event.setTarget(expressionEvaluator.getStringExpression(executionObject.getExeclog().target()));
		event.setTargetMethod(expressionEvaluator.getStringExpression(executionObject.getExeclog().targetMethod()));
		return event;
	}

	public ExecutionEvent end(ExecutionEvent event, Object result, Exception exception) {
		executionObject.setResult(result);
		executionObject.setE(exception);
		ExecutionExpressionEvaluator expressionEvaluator = new ExecutionExpressionEvaluator(executionObject, SpringUtil.getApplicationContext());
		event.setTargetTimestamp(SystemUtils.getCurrentTime());
		event.setTargetMessage(expressionEvaluator.getStringExpression(executionObject.getExeclog().targetMessage()));
		event.setExceptionMessage(expressionEvaluator.getStringExpression(executionObject.getExeclog().exceptionMessage()));
		event.setStatus(expressionEvaluator.getBooleanExpression(executionObject.getExeclog().success()) ? "SUCCESS" : "ERROR");
		return event;
	}

	/**
	 * @param event
	 */
	public void append(ExecutionEvent event) {
		executionEvents.add(event);
	}

	/**
	 * @param consumers
	 */
	public void triggerEvents(List<Consumer<ExecutionEvent>> consumers) {
		executionEvents.forEach(event -> consumers.forEach(c -> c.accept(event)));
	}

	@Getter
	static class ExecutionRootObject {

		private Execlog execlog;

		private final Method method;

		private final Object[] args;

		private final Object source;

		private Class<?> sourceClass;

		private final Class<?> targetClass;

		private final Method targetMethod;

		private Object result;
		private Exception e;

		public ExecutionRootObject(Execlog execlog, Method method, Object[] args, Object source, Class<?> sourceClass, Class<?> targetClass,
				Method targetMethod) {
			this.execlog = execlog;
			this.method = method;
			this.args = args;
			this.source = source;
			this.sourceClass = sourceClass;
			this.targetClass = targetClass;
			this.targetMethod = targetMethod;
		}

		/**
		 * @param result the result to set
		 */
		public void setResult(Object result) {
			this.result = result;
		}

		/**
		 * @param ex the ex to set
		 */
		public void setE(Exception ex) {
			this.e = ex;
		}
	}

	static class ExecutionExpressionEvaluator extends CachedExpressionEvaluator {

		private MethodBasedEvaluationContext evaluationContext;
		private static final ObjectMapper MAPPER = new ObjectMapper();

		public ExecutionExpressionEvaluator(ExecutionRootObject rootObject, @Nullable BeanFactory beanFactory) {
			this.evaluationContext = new MethodBasedEvaluationContext(rootObject, rootObject.getTargetMethod(), rootObject.getArgs(),
					getParameterNameDiscoverer());
		}

		public String getStringExpression(String expression) {
			try {
				if (StringUtils.isEmpty(expression)) {
					return expression;
				}
				Object object = this.getParser().parseExpression(expression).getValue(evaluationContext, Object.class);
				if (object == null) {
					return null;
				}
				if (object instanceof String) {
					return (String) object;
				}
				if (object instanceof Throwable) {
					return ExceptionUtils.getStackTrace((Throwable) object);
				}
				return MAPPER.writeValueAsString(object);
			} catch (Exception ex) {
				log.error("getStringExpression {} error: ", expression, ex);
			}
			return expression;
		}

		public boolean getBooleanExpression(String expression) {
			return this.getParser().parseExpression(expression).getValue(evaluationContext, Boolean.class);
		}
	}

	/**
	 * @param joinPoint
	 */
	public void setRootObject(ProceedingJoinPoint joinPoint, Execlog execlog) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		method = BridgeMethodResolver.findBridgedMethod(method);
		Object source = joinPoint.getTarget();
		Class<?> sourceClass = method.getDeclaringClass();
		Object[] args = joinPoint.getArgs();
		Class<?> targetClass = AopProxyUtils.ultimateTargetClass(source);
		Method targetMethod = (!Proxy.isProxyClass(targetClass) ? AopUtils.getMostSpecificMethod(method, targetClass) : method);
		this.executionObject = new ExecutionRootObject(execlog, method, args, source, sourceClass, targetClass, targetMethod);
	}
}
