package org.macula.plugin.dataset.util;

import org.macula.plugin.core.utils.StringUtils;

public class QueryReferenceUtils {

	public static final String REF_PREFIX = "[ref=";
	public static final String REF_SUFFIX = "]";

	public static boolean isReferenceValue(String value) {
		String trimValue = StringUtils.trim(value);
		return StringUtils.startsWith(trimValue, REF_PREFIX) && StringUtils.endsWith(trimValue, REF_SUFFIX);
	}

	public static String getReferenceCode(String value) {
		if (!isReferenceValue(value)) {
			return null;
		}
		String trimValue = StringUtils.trim(value);
		int length = trimValue.length();
		String refValue = trimValue.substring(5, length - 1);
		return StringUtils.trim(refValue);
	}
}
