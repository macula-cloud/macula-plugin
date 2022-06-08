package org.macula.plugin.dataset.loader;

import org.springframework.core.Ordered;

/**
 * <p>
 * <b>DataLoader</b> 数据配置加载接口
 * </p>
 */
public interface DataLoader<T> extends Ordered {
	/**
	 * 根据code加载Data
	 * @param code 代码
	 * @return T
	 */
	public T loader(String code);

	/**
	 * 将缓存的data清除
	 */
	public void refresh();
}
