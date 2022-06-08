package org.macula.plugin.dataset.value.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.macula.plugin.dataset.value.ValueTypeConvert;

import org.springframework.stereotype.Component;

/**
 * <p> <b>DefaultValueTypeConvert</b> 是查询结果的转化实现. </p>
 */
@Component
public class DefaultValueTypeConvert implements ValueTypeConvert {

	@Override
	public Serializable convertCollection(Collection<?> result) {

		if (result.isEmpty()) {
			return null;
		}

		if (result.size() == 1) {
			return convertSingleObject(result.iterator().next());
		}

		ArrayList<Object> list = new ArrayList<Object>();

		for (Object object : result) {
			list.add(convertSingleObject(object));
		}

		return list;
	}

	@Override
	public Serializable convertMap(Map<String, ?> result) {

		if (result.size() == 0) {
			return null;
		}

		if (result.size() == 1) {
			return convertCollection(result.values());
		}

		HashMap<String, Object> map = new HashMap<String, Object>();

		for (Map.Entry<String, ?> entry : result.entrySet()) {
			map.put(entry.getKey(), convertSingleObject(entry.getValue()));
		}

		return map;
	}

	@Override
	public Serializable convertCollectionMap(Collection<Map<String, ?>> result) {

		if (result == null) {
			return null;
		}

		if (result.size() == 1) {
			return convertMap(result.iterator().next());
		}

		ArrayList<Object> list = new ArrayList<Object>();

		for (Map<String, ?> object : result) {
			list.add(convertMap(object));
		}

		return list;

	}

	@Override
	@SuppressWarnings("unchecked")
	public Serializable convertSingleObject(Object object) {

		if (object == null) {
			return null;
		}

		if (object instanceof String) {
			return object.toString();
		}

		if (object instanceof Number) {
			return (Number) object;
		}

		if (object instanceof Date) {
			return (Date) object;
		}

		if (object.getClass().isArray()) {
			Object[] array = (Object[]) object;
			if (array.length == 2 && array[0] instanceof String) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put((String) array[0], array[1]);
				return map;
			}
			return convertCollection(Arrays.asList((Object[]) object));
		}

		if (object instanceof Collection) {
			return convertCollection((Collection<?>) object);
		}
		if (object instanceof Map) {
			return convertMap((Map<String, ?>) object);
		}

		return object.toString();
	}
}
