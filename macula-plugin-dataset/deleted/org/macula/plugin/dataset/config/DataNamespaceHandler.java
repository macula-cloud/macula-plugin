package org.macula.plugin.dataset.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * <p>
 * <b>BaseNamespaceHandler</b> base命名空间的处理类
 * </p>
 */
public class DataNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("dataset", new DataSetBeanDefinitionParser());
		registerBeanDefinitionParser("datasource", new DataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("dataparam", new DataParamBeanDefinitionParser());
	}
}
