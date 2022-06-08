package org.macula.plugin.dataset.service;

import java.util.List;
import java.util.Locale;

import org.macula.plugin.dataset.domain.DataEnum;

/**
 * <p> <b>DataEnumService</b> 是枚举服务类. </p>
 */
public interface DataEnumService {

	/**
	 * @param type
	 *            枚举类型
	 * @param locale
	 *            区域
	 * @return DataEnum 列表
	 */
	List<? extends DataEnum> findEnabledEnums(String type, Locale locale);

	/**
	 * @param type
	 *            枚举类型
	 * @param code
	 *            代码
	 * @param locale
	 *            区域
	 * @return 显示值
	 */
	String getEnumCode2Value(String type, String code, Locale locale);

}
