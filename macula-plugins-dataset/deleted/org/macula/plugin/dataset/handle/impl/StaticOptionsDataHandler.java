package org.macula.plugin.dataset.handle.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.macula.cloud.api.protocol.DataType;
import org.macula.cloud.api.utils.ConversionUtils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * <p> <b>StaticOptionsDataHandler</b> 是构建成选择项的处理器. </p>
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class StaticOptionsDataHandler extends AbstractDataHandler {

	private static final String NAME = "StaticOptionsDataHandler";
	public static final String TARGET_CLASS = "_target_class_";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Class<?> getInputClass() {
		return String.class;
	}

	@Override
	public Class<?> getOutputClass() {
		return Object.class;
	}

	@Override
	protected Object handleInternal(Object data, Object userContext) {
		Class<?> targetClass = (Class<?>) getExtractParams().get(TARGET_CLASS);
		if (targetClass == null) {
			targetClass = String.class;
		}
		List<Object[]> options = new LinkedList<Object[]>();

		String originValue = data.toString();
		if (targetClass == DataType.class) {
			options.add(new Object[] {
					originValue });
			return options;
		}

		StringTokenizer st = new StringTokenizer(originValue, "|");
		while (st.hasMoreTokens()) {
			String[] tokenValues = st.nextToken().split(":");
			Object[] convertValues = new Object[tokenValues.length];
			Object code = ConversionUtils.convertQuietly(tokenValues[0], targetClass);
			convertValues[0] = code;
			if (tokenValues.length > 1) {
				System.arraycopy(tokenValues, 1, convertValues, 1, tokenValues.length - 1);
			}
			options.add(convertValues);
		}
		return options;
	}
}
