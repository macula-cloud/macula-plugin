package org.macula.plugin.dataset.handle;

import java.util.Map;
import java.util.Properties;

import org.springframework.data.domain.Pageable;

/**
 * <p> <b>DataHandler</b> 数据处理器. </p>
 */
public interface DataHandler {

	/**
	 * 转换器名称.
	 */
	String getName();

	/**
	 * 可转化的入口数据类型.
	 */
	Class<?> getInputClass();

	/**
	 * 转化后的结果类型.
	 */
	Class<?> getOutputClass();

	/**
	 * 获取转接过程中生成的参数.
	 */
	Map<String, Object> getOutputParameters();

	/**
	 * 设置上一次转换生成的参数，作为本次转换的已知参数.
	 */
	void setInputParameters(Map<String, Object> parameters);

	/**
	 * 设置数据源.
	 */
	void setDataSource(Object dataSource);

	/**
	 * 从Properties中读取所需的值.
	 */
	void initialize(Properties properties);

	/**
	 * 获取配置属性.
	 */
	Properties getProperties();

	/**
	 * 设置分页信息.
	 */
	void setPageable(Pageable pageable);

	/**
	 * 将指定的字符串按需要的处理解析.
	 */
	Object handle(Object data, Object userContext);

}
