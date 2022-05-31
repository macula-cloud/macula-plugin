package org.macula.plugin.dataset.handle.impl;

/**
 * <p> <b>StringDataHandler</b> 是字符串输入输出转换. </p>
 */
public abstract class StringDataHandler extends AbstractDataHandler {

	@Override
	public Class<?> getInputClass() {
		return String.class;
	}

	@Override
	public Class<?> getOutputClass() {
		return String.class;
	}

}
