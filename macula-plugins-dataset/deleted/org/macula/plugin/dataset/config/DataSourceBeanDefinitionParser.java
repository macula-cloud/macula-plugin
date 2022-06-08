package org.macula.plugin.dataset.config;

import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.domain.DataSource;
import org.macula.plugin.dataset.domain.DataSourceType;
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
 * <b>DataSourceBeanDefinitionParser</b> DataSource的配置文件解析
 * </p>
 *
 */
public class DataSourceBeanDefinitionParser implements BeanDefinitionParser {

	private static final String ID_ATTRIBUTE = "id";
	private static final String NAME_ATTRIBUTE = "name";
	/** 数据源类型 */
	private static final String DATA_SOURCE_TYPE_ELEMENT = "dataSourceType";
	/** JDBC驱动 */
	private static final String DRIVER_ELEMENT = "driver";
	/** JDBC URL地址 */
	private static final String URL_ELEMENT = "url";
	/** 数据库用户名 */
	private static final String USERNAME_ELEMENT = "username";
	/** 数据库密码 */
	private static final String PASSWORD_ELEMENT = "password";
	/** 是否使用jndi */
	private static final String JNDI_ELEMENT = "jndi";
	/** 最大连接数 */
	private static final String MAX_SIZE_ELEMENT = "maxSize";
	/** 最大空闲连接数 */
	private static final String MAX_IDLE_ELEMENT = "maxIdle";
	/** 最大活动时间 */
	private static final String MAX_ACTIVE_ELEMENT = "maxActive";
	/** 最大等待时间 */
	private static final String MAX_WAIT_ELEMENT = "maxWait";
	/** 验证语句 */
	private static final String VALIDATION_QUERY_ELEMENT = "validationQuery";

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute(ID_ATTRIBUTE);
		if (StringUtils.isNotEmpty(id)) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class);
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

					// 是否使用jndi
					if (JNDI_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(JNDI_ELEMENT, Boolean.valueOf(nodeContent));
					}

					//  数据源类型
					if (DATA_SOURCE_TYPE_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(DATA_SOURCE_TYPE_ELEMENT, DataSourceType.valueOf(nodeContent));
					}

					// JDBC URL地址 
					if (URL_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(URL_ELEMENT, nodeContent);
					}

					// JDBC驱动 
					if (DRIVER_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(DRIVER_ELEMENT, nodeContent);
					}

					// 数据库用户名 
					if (USERNAME_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(USERNAME_ELEMENT, nodeContent);
					}

					// 数据库密码 
					if (PASSWORD_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(PASSWORD_ELEMENT, nodeContent);
					}

					// 最大连接数
					if (MAX_SIZE_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(MAX_SIZE_ELEMENT, Integer.valueOf(nodeContent));
						}
					}

					// 最大空闲连接数 
					if (MAX_IDLE_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(MAX_IDLE_ELEMENT, Integer.valueOf(nodeContent));
						}
					}

					// 最大活动时间 
					if (MAX_ACTIVE_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(MAX_ACTIVE_ELEMENT, Integer.valueOf(nodeContent));
						}
					}

					// 最大等待时间 
					if (MAX_WAIT_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							builder.addPropertyValue(MAX_WAIT_ELEMENT, Integer.valueOf(nodeContent));
						}
					}

					// 验证语句 
					if (VALIDATION_QUERY_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(VALIDATION_QUERY_ELEMENT, nodeContent);
					}
				}
			}
			parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getRawBeanDefinition(), id));
		}
		return null;
	}

}
