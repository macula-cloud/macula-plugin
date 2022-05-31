package org.macula.plugin.dataset.config;

import org.macula.cloud.api.context.CloudApplicationContext;
import org.macula.cloud.api.protocol.DataType;
import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.domain.DataParam;
import org.macula.plugin.dataset.domain.DataSource;
import org.macula.plugin.dataset.service.DataSourceService;
import org.macula.plugin.dataset.value.scope.ValueScope;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * <p>
 * <b>DataParamBeanDefinitionParser</b> 数据参数的XML配置解析
 * </p>
 */
public class DataParamBeanDefinitionParser implements BeanDefinitionParser {

	private static final String ID_ATTRIBUTE = "id";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String TYPE_ELEMENT = "type";
	private static final String VALUE_ELEMENT = "value";
	private static final String VALUE_SCOPE_ELEMENT = "valueScope";
	private static final String DATA_TYPE_ELEMENT = "dataType";
	private static final String DATA_SOURCE_ELEMENT = "dataSource";
	private static final String ENABLED_ELEMENT = "enabled";
	private static final String COMMENTS_ELEMENT = "comments";
	private static final String ORDERED_ELEMENT = "ordered";

	private DataSourceService dataSourceService;

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {

		String id = element.getAttribute(ID_ATTRIBUTE);
		if (StringUtils.isNotEmpty(id)) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataParam.class);
			builder.setScope(BeanDefinition.SCOPE_SINGLETON);
			builder.setLazyInit(true);

			builder.addPropertyValue("code", id);
			builder.addPropertyValue("name", element.getAttribute(NAME_ATTRIBUTE));
			NodeList nodes = element.getChildNodes();
			for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
				Node node = nodes.item(i);
				if (Node.ELEMENT_NODE == node.getNodeType()) {
					String nodeName = node.getLocalName();
					String nodeContent = StringUtils.stripContent(node.getTextContent());

					// 参数分类
					if (TYPE_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(TYPE_ELEMENT, nodeContent);
					}

					// 参数值
					if (VALUE_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(VALUE_ELEMENT, nodeContent);
					}

					// 生存范围
					if (VALUE_SCOPE_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(VALUE_SCOPE_ELEMENT, ValueScope.valueOf(nodeContent));
						} else {
							builder.addPropertyValue(VALUE_SCOPE_ELEMENT, ValueScope.NONE);
						}
					}

					// 数据类型
					if (DATA_TYPE_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(DATA_TYPE_ELEMENT, DataType.valueOf(nodeContent));
					}

					// 数据源
					if (DATA_SOURCE_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							if (null == dataSourceService) {
								dataSourceService = CloudApplicationContext.getBean(DataSourceService.class);
							}
							DataSource dataSource = dataSourceService.findByCode(nodeContent);
							builder.addPropertyValue(DATA_SOURCE_ELEMENT, dataSource);
						}
					}

					// 是否生效
					if (ENABLED_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(ENABLED_ELEMENT, Boolean.valueOf(nodeContent));
						} else {
							builder.addPropertyValue(ENABLED_ELEMENT, true);
						}
					}

					// 备注
					if (COMMENTS_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(COMMENTS_ELEMENT, nodeContent);
					}

					// 显示顺序
					if (ORDERED_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(ORDERED_ELEMENT, Integer.valueOf(nodeContent));
						} else {
							builder.addPropertyValue(ORDERED_ELEMENT, 0);
						}
					}
				}
			}
			parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getRawBeanDefinition(), id));
		}
		return null;
	}

}
