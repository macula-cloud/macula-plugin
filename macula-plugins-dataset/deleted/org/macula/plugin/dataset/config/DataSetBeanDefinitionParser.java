package org.macula.plugin.dataset.config;

import java.util.ArrayList;
import java.util.List;

import org.macula.cloud.api.context.CloudApplicationContext;
import org.macula.cloud.api.protocol.DataType;
import org.macula.cloud.api.protocol.FieldControl;
import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.domain.DataArg;
import org.macula.plugin.dataset.domain.DataParam;
import org.macula.plugin.dataset.domain.DataSet;
import org.macula.plugin.dataset.domain.DataSource;
import org.macula.plugin.dataset.service.DataParamService;
import org.macula.plugin.dataset.service.DataSourceService;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

/**
 * <p> <b>DataSetBeanDefinitionParser</b> DataSet的XML配置文件解析 </p>
 */
public class DataSetBeanDefinitionParser implements BeanDefinitionParser {

	private static final String ID_ATTRIBUTE = "id";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String EXPRESSION_TEXT_ELEMENT = "expressionText";
	private static final String PAGABLE_ELEMENT = "pagable";
	private static final String HANDLER_CHAIN_ELEMENT = "handlerChain";
	private static final String DATA_SOURCE_ELEMENT = "dataSource";
	private static final String DATA_ARGS_ELEMENT = "dataArgs";

	private static final String DATA_ARG_ELEMENT = "dataArg";

	private static final String LABEL_ATTRIBUTE = "label";
	private static final String DATA_TYPE_ELEMENT = "dataType";
	private static final String FIELD_CONTROL_ELEMENT = "fieldControl";
	private static final String ALLOW_NULL_ELEMENT = "allowNull";
	private static final String DEFAULT_VALUE_ELEMENT = "defaultValue";
	private static final String DATA_PARAM_ELEMENT = "dataParam";

	private DataSourceService dataSourceService;

	private DataParamService dataParamService;

	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
		String id = element.getAttribute(ID_ATTRIBUTE);
		if (StringUtils.isNotEmpty(id)) {
			BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DataSet.class);
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

					// expressionText
					if (EXPRESSION_TEXT_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(EXPRESSION_TEXT_ELEMENT, nodeContent);
					}

					// pagable
					if (PAGABLE_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(PAGABLE_ELEMENT, nodeContent);
					}

					// handlerChain
					if (HANDLER_CHAIN_ELEMENT.equals(nodeName)) {
						builder.addPropertyValue(HANDLER_CHAIN_ELEMENT, nodeContent);
					}

					// dataSource
					if (DATA_SOURCE_ELEMENT.equals(nodeName)) {
						if (StringUtils.isNotEmpty(nodeContent)) {
							if (null == dataSourceService) {
								dataSourceService = CloudApplicationContext.getBean(DataSourceService.class);
							}
							DataSource dataSource = dataSourceService.findByCode(nodeContent);
							builder.addPropertyValue(DATA_SOURCE_ELEMENT, dataSource);
						}
					}

					// dataArgs
					if (DATA_ARGS_ELEMENT.equals(nodeName)) {
						List<DataArg> dataArgList = new ArrayList<DataArg>();
						//////////////////////////////////////////////////////////////////////////////////////////
						// 处理DataArgs属性
						NodeList dataArgNodeList = node.getChildNodes();
						for (int d = 0; dataArgNodeList != null && d < dataArgNodeList.getLength(); d++) {
							Node dataArgNode = dataArgNodeList.item(d);
							if (Node.ELEMENT_NODE == dataArgNode.getNodeType()) {
								String dataArgNodeName = dataArgNode.getLocalName();
								// DataArg
								if (DATA_ARG_ELEMENT.equals(dataArgNodeName)) {
									dataArgList.add(parseDataArg(dataArgNode));
								}
							}
						}
						/////////////////////////////////////////////////////////////////////////////////////////////

						builder.addPropertyValue(DATA_ARGS_ELEMENT, dataArgList);
					}
				}
			}
			parserContext.registerBeanComponent(new BeanComponentDefinition(builder.getRawBeanDefinition(), id));
		}
		return null;
	}

	/*
	 * 解析DataArg这个配置
	 * @param element
	 * @return
	 */
	private DataArg parseDataArg(Node dataArgNode) {

		Element element = (Element) dataArgNode;

		DataArg dataArg = new DataArg();

		dataArg.setLabel(element.getAttribute(LABEL_ATTRIBUTE));
		dataArg.setName(element.getAttribute(NAME_ATTRIBUTE));

		NodeList nodeList = element.getChildNodes();

		for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (Node.ELEMENT_NODE == node.getNodeType()) {
				String nodeName = node.getLocalName();
				String nodeContent = StringUtils.stripContent(node.getTextContent());

				// DataType
				if (DATA_TYPE_ELEMENT.equals(nodeName)) {
					if (StringUtils.isNotEmpty(nodeContent)) {
						dataArg.setDataType(DataType.valueOf(nodeContent));
					}
				}

				// FieldControl
				if (FIELD_CONTROL_ELEMENT.equals(nodeName)) {
					if (StringUtils.isNotEmpty(nodeContent)) {
						dataArg.setFieldControl(FieldControl.valueOf(nodeContent));
					}
				}

				// allowNull
				if (ALLOW_NULL_ELEMENT.equals(nodeName)) {
					if (StringUtils.isNotEmpty(nodeContent)) {
						dataArg.setAllowNull(Boolean.valueOf(nodeContent));
					}
				}

				// defaultValue
				if (DEFAULT_VALUE_ELEMENT.equals(nodeName)) {
					if (StringUtils.isNotEmpty(nodeContent)) {
						dataArg.setDefaultValue(nodeContent);
					}
				}

				// dataParam
				if (DATA_PARAM_ELEMENT.equals(nodeName)) {
					if (StringUtils.isNotEmpty(nodeContent)) {
						if (null == dataParamService) {
							dataParamService = CloudApplicationContext.getBean(DataParamService.class);
						}
						DataParam dataParam = dataParamService.findByCode(nodeContent);
						dataArg.setDataParam(dataParam);
					}
				}
			}
		}
		return dataArg;
	}
}
