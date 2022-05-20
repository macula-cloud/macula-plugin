package org.macula.plugin.dataset.service;

import org.macula.plugin.dataset.domain.DataParam;

/**
 * <p>
 * <b>DataParamService</b> 数据参数Service
 * </p>
 */
public interface DataParamService {

	/**
	 * 根据代码获取参数配置
	 * @param dataParamCode 参数代码
	 * @return DataParam
	 */
	DataParam findByCode(String dataParamCode);

}
