package org.macula.plugin.dataset.value;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * <p> <b>ValueTypeConvert</b> 是查询结果集的转化接口. </p>
 * 
 */
public interface ValueTypeConvert {

	/** 将集合转化为可序列对象 */
	Serializable convertCollection(Collection<?> result);

	/** 将Map转化为可序列对象 */
	Serializable convertMap(Map<String, ?> result);

	/** 将Map<Collection>转化为可序列对象 */
	Serializable convertCollectionMap(Collection<Map<String, ?>> result);

	/** 将单一对象转为可序列对象 */
	Serializable convertSingleObject(Object object);
}
