package org.macula.plugin.dataset.handle;

import java.util.Properties;

/**
 * <p>
 * <b>HandlerEntry</b> 是持久化使用的辅助类.
 * </p>
 * 
 */
public class HandlerEntry {

	private String className;
	private Properties properties;

	public HandlerEntry() {
	}

	/**
	 * @param className 类名
	 * @param properties 额外参数
	 */
	public HandlerEntry(String className, Properties properties) {
		this.className = className;
		this.properties = properties;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

}