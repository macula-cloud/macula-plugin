package org.macula.plugin.dataset.service;

import org.macula.plugin.dataset.domain.DataSet;

/**
 * <p>
 * <b>DataSetService</b> 数据集Service
 * </p>
 *
 */
public interface DataSetService {
	/**
	 * 根据代码查询DataSet
	 * @param dataSetCode
	 * @return DataSet
	 */
	public DataSet findByCode(String dataSetCode);
}
