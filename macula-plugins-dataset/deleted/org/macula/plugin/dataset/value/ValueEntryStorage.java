package org.macula.plugin.dataset.value;

import org.macula.plugin.dataset.value.scope.ValueScope;

/**
 * <p> <b>ValueEntryStorage</b> 数据的缓存策略. </p>
 * 
 */
public interface ValueEntryStorage {

	/**
	 * 存入数据项
	 */
	ValueEntry store(ValueEntry valueEntry);

	/**
	 * 取出数据项
	 */
	ValueEntry retrieve(String key);

	/**
	 * 取出数据项
	 */
	ValueEntry retrieve(String key, ValueScope valueScope);

	/**
	 * 清除指定范围的值数据.
	 */
	void cleanup(ValueScope... valueScope);

	/**
	 * 清除Key值数据.
	 */
	void remove(String... keys);

	/**
	 * 清除指定范围的Key值数据.
	 */
	void remove(String key, ValueScope scope);

	/**
	 * 清除指定的值数据.
	 */
	void remove(ValueEntry... valueEntries);

}
