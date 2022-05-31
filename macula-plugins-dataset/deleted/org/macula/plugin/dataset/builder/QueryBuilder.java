package org.macula.plugin.dataset.builder;

import java.util.Map;

/**
 * <p> <b>QueryBuilder</b> 构建查询的接口. </p>
 */
public interface QueryBuilder {

	/**
	 * 获取原查询语句.
	 */
	String getQuery();

	/**
	 * 获取参数值
	 */
	Map<String, Object> getParams();

}
