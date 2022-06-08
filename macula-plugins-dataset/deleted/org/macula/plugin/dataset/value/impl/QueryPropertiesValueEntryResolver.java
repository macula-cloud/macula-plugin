package org.macula.plugin.dataset.value.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.macula.plugin.dataset.builder.QueryBuilder;
import org.macula.plugin.dataset.builder.SimpleQueryBuilder;
import org.macula.plugin.dataset.value.ValueTypeConvert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * <p> <b>QueryPropertiesValueEntryResolver</b> 读取相应的属性文件，其值认为是可被执行的SQL语句. </p>
 */
public final class QueryPropertiesValueEntryResolver extends PropertiesValueEntryResolver {

	@Autowired
	private ValueTypeConvert valueTypeConvert;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 * 构造
	 * 
	 * @param locations
	 *            属性文件位置列表
	 * @param dataSource
	 *            使用的数据源
	 */
	public QueryPropertiesValueEntryResolver(List<String> locations, DataSource dataSource) {
		super(locations);
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	protected Serializable resolveInternal(String originValue, Object userContext) {

		QueryBuilder builder = new SimpleQueryBuilder(originValue, userContext);
		String resolvedQuery = builder.getQuery();
		Map<String, Object> resolvedParams = builder.getParams();
		List<Map<String, Object>> content = jdbcTemplate.queryForList(resolvedQuery, resolvedParams);
		return valueTypeConvert.convertCollection(content);
	}

}
