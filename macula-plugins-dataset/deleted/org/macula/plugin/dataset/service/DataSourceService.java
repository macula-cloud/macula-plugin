package org.macula.plugin.dataset.service;

import org.macula.plugin.dataset.domain.DataSource;

/**
 * <p>
 * <b>DataSourceService</b> 数据源Service
 * </p>
 */
public interface DataSourceService {

	/**
	 * 根据代码获取数据源配置
	 * @param dataSourceCode 数据源代码
	 * @return DataSource
	 */
	DataSource findByCode(String dataSourceCode);
}
