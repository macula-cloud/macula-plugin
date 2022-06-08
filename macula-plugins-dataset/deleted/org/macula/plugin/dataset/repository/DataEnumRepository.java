package org.macula.plugin.dataset.repository;

import java.util.List;

import org.macula.plugin.dataset.domain.DataEnum;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <p> <b>DataEnumRepository</b> 是JpaBaseEnum的存取接口. </p>
 */
public interface DataEnumRepository extends JpaRepository<DataEnum, Long> {

	/**
	 * @param type
	 *            类型
	 * @param locale
	 *            区域
	 * @param enabled
	 *            是否有效
	 * @return JpaDataEnum列表
	 */
	List<DataEnum> findByTypeAndLocaleAndEnabledOrderByOrderedAsc(String type, String locale, boolean enabled);

	/**
	 * @param type
	 *            类型
	 * @param code
	 *            代码
	 * @param locale
	 *            区域
	 * @param enabled
	 *            是否有效
	 * @return 单个JpaDataEnum
	 */
	DataEnum getByTypeAndCodeAndLocaleAndEnabled(String type, String code, String locale, boolean enabled);

}
