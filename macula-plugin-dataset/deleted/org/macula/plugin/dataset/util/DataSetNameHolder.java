/**
 * RepositoryMethodNameHolder.java 2016年6月22日
 */
package org.macula.plugin.dataset.util;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * <b>DataSetNameHolder</b> 存放当前线程的DataSet名称
 * </p>
 *
 * @since 2016年6月22日
 * @author Rain
 * @version $Id: DataSetNameHolder.java 6194 2016-07-07 12:13:21Z wzp $
 */
public class DataSetNameHolder {
	private static ThreadLocal<String> dataSetName = new ThreadLocal<String>();

	public static void set(String name) {
		if (StringUtils.isNotEmpty(name)) {
			dataSetName.set("DataSet." + name);
		}
	}

	public static String get() {
		return dataSetName.get();
	}

	public static void remove() {
		dataSetName.remove();
	}
}