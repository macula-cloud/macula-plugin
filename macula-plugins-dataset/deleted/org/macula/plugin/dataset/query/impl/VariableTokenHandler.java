package org.macula.plugin.dataset.query.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.query.TokenHandler;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.ExpressionUtils;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <p> <b>VariableTokenHandler</b> 是变量替换法则.
 * 
 * 只支持简单的Collection、String、Number，不支持Date以及其他不能转化为String的类型.
 * 
 * 
 * IMPORTANT: 该方法存在SQL注入风险，如果需要较强的SQL注入保护，请使用#()#，而不是#[]# </p>
 * 
 */
public class VariableTokenHandler implements TokenHandler {

	private final Object userContext;
	private final SpelExpressionParser parser = new SpelExpressionParser();

	public VariableTokenHandler(Object userContext) {
		this.userContext = userContext;
	}

	@Override
	public String handleToken(final String content) {

		String contentValue = content;

		boolean useQuote = isUseQuote(contentValue);
		if (useQuote) {
			contentValue = contentValue.substring(1, contentValue.length() - 1);
		}

		Expression expression = parser.parseExpression(contentValue);
		EvaluationContext evaluationContext = new StandardEvaluationContext(userContext);

		Object result = expression.getValue(evaluationContext);

		if (result instanceof Collection) {
			List<String> stringList = new ArrayList<String>();
			for (Object item : (Collection<?>) result) {
				String itemValue = ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(item),
						String.class);
				itemValue = filter(itemValue, useQuote);
				stringList.add(itemValue);
			}
			if (stringList.isEmpty()) {
				stringList.add(filter(StringUtils.EMPTY, useQuote));
			}
			result = stringList;
			return ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(result), String.class);
		}

		return filter(ExpressionUtils.convertTypedValue(evaluationContext, new TypedValue(result), String.class),
				useQuote);
	}

	protected boolean isUseQuote(String content) {
		return content.startsWith("'") && content.endsWith("'");
	}

	protected String filter(String content, boolean useQuote) {
		String contentValue = filterInject(content);
		return useQuote ? "'" + contentValue + "'" : contentValue;
	}

	protected String filterInject(String content) {
		if (StringUtils.isBlank(content)) {
			return content;
		}
		return content.replaceAll("'", "''");
	}

}
