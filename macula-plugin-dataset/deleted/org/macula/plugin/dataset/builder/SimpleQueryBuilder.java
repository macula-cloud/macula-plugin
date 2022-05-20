package org.macula.plugin.dataset.builder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.macula.plugin.core.principal.SubjectPrincipal;
import org.macula.plugin.dataset.query.GenericTokenParser;
import org.macula.plugin.dataset.query.TokenHandler;
import org.macula.plugin.dataset.query.TokenHolder;
import org.macula.plugin.dataset.query.impl.DataParamTokenHandler;
import org.macula.plugin.dataset.query.impl.ParameterTokenHandler;
import org.macula.plugin.dataset.query.impl.VariableTokenHandler;

/**
 * <p> <b>SimpleQueryBuilder</b> QueryBuilder的默认实现类. </p>
 */
public class SimpleQueryBuilder implements QueryBuilder {

	/**
	 * 翻译后的SQL
	 */
	private String query;

	/**
	 * 提取的参数
	 */
	private final Map<String, Object> extractParams;

	private final Map<TokenHolder, TokenHandler> tokenHandlers;

	/**
	 * 构造
	 */
	public SimpleQueryBuilder(String query, SubjectPrincipal userContext) {
		this.query = query;
		this.extractParams = new HashMap<String, Object>();

		tokenHandlers = new LinkedHashMap<TokenHolder, TokenHandler>();
		tokenHandlers.put(new TokenHolder("#[", "]#"), new VariableTokenHandler(userContext));
		tokenHandlers.put(new TokenHolder("#(", ")#"), new ParameterTokenHandler(userContext, extractParams));
		tokenHandlers.put(new TokenHolder("#P(", ")#"), new DataParamTokenHandler(userContext, extractParams));

		build();
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public Map<String, Object> getParams() {
		return this.extractParams;
	}

	private void build() {
		for (Map.Entry<TokenHolder, TokenHandler> entry : tokenHandlers.entrySet()) {
			TokenHolder holder = entry.getKey();
			GenericTokenParser parse = new GenericTokenParser(holder.getOpenToken(), holder.getCloseToken(), entry.getValue());
			query = parse.parse(query);
		}
	}
}
