package org.macula.plugin.dataset.query.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.query.TokenHandler;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <p> <b>ParameterTokenHandler</b> 是参数处理. </p>
 */
public class ParameterTokenHandler implements TokenHandler {

	private final Object userContext;
	private final Map<String, Object> dataContext;
	private final SpelExpressionParser parser = new SpelExpressionParser();

	public ParameterTokenHandler(Object userContext, Map<String, Object> params) {
		this.userContext = userContext;
		this.dataContext = params;
	}

	@Override
	public String handleToken(String content) {
		Expression expression = parser.parseExpression(content);
		EvaluationContext evaluationContext = new StandardEvaluationContext(userContext);
		Serializable result = (Serializable) expression.getValue(evaluationContext, Object.class);
		String paramName = generateNextParamName(content, dataContext);
		if (result instanceof Collection && ((Collection<?>) result).isEmpty()) {
			result = (Serializable) Arrays.asList(StringUtils.EMPTY);
		}
		dataContext.put(paramName, result);
		return ":" + paramName;
	}

	protected String generateNextParamName(String content, Map<String, Object> dataContext) {
		int index = dataContext.size();
		return "ptoken" + index;
	}
}
