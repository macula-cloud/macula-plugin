package org.macula.plugin.execlog.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.lang.Nullable;

@Slf4j
public class ServiceInvokeExpressionEvaluator extends CachedExpressionEvaluator {

	private MethodBasedEvaluationContext evaluationContext;
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public ServiceInvokeExpressionEvaluator(ServiceInvokeRootObject rootObject, @Nullable BeanFactory beanFactory) {
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
