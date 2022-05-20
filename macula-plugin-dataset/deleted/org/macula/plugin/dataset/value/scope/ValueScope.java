package org.macula.plugin.dataset.value.scope;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> <b>ValueScope</b> 是数据作用范围的枚举. </p>
 * 
 */
public enum ValueScope {

	/**
	 * 不缓存
	 */
	NONE("NULL"),
	/**
	 * 整个用户Session作用域.
	 */
	SESSION("SESSION"),
	/**
	 * 实例级作用域.
	 */
	INSTANCE("INSTANCE"),
	/**
	 * 全局级别作用域.
	 */
	APPLICATION("APPLICATION");

	private final String value;

	private ValueScope(String value) {
		this.value = value;
	}

	public String getCacheName() {
		return this.value;
	}

	public static ValueScope obtainValueScope(String property) {
		for (ValueScope scope : ValueScope.values()) {
			if (property.startsWith(scope.toString() + ".")) {
				return scope;
			}
		}
		return ValueScope.NONE;
	}

	public static List<String> createScopesProperties(String property) {
		List<String> properties = new ArrayList<String>();
		for (ValueScope scope : ValueScope.values()) {
			properties.add(scope == NONE ? property : scope.toString() + "." + property);
		}
		return properties;
	}

}
