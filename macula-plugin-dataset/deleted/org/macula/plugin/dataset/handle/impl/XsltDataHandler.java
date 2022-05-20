package org.macula.plugin.dataset.handle.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import lombok.extern.slf4j.Slf4j;
import org.macula.cloud.api.exception.MaculaCloudException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * <p> <b>XsltDataHandler</b> 是XSLT转化的数据处理器. </p>
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class XsltDataHandler extends StringDataHandler implements ResourceLoaderAware {

	private String xstlTemplate;
	private ResourceLoader resourceLoader;
	private final TransformerFactory factory = TransformerFactory.newInstance();

	public static final String NAME = "QueryParserDataHandler";
	public static final String XSTL_PARAM_NAME = "XSTL_TEMPLATE_LOCATION";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void initialize(Properties properties) {
		xstlTemplate = properties.getProperty(XSTL_PARAM_NAME);
	}

	@Override
	public Properties getProperties() {
		Properties properties = new Properties();
		properties.setProperty(XSTL_PARAM_NAME, xstlTemplate);
		return properties;
	}

	@Override
	public String handleInternal(Object data, Object userContext) {
		Assert.notNull(xstlTemplate, "XstlTemplate can't be null!");
		Resource resource = resourceLoader.getResource(xstlTemplate);
		Assert.notNull(resource, xstlTemplate + " can't load any resource!");
		try {
			Writer writer = new StringWriter();
			Templates templates = factory.newTemplates(new StreamSource(resource.getInputStream()));
			Transformer transformer = templates.newTransformer();
			Source xmlSource = new StreamSource(new StringReader(data.toString()));
			transformer.transform(xmlSource, new StreamResult(writer));
			return writer.toString();
		} catch (TransformerConfigurationException e) {
			log.error("", e);
			throw new MaculaCloudException("macula.base.data.xstl.configexception", e);
		} catch (IOException e) {
			log.error("", e);
			throw new MaculaCloudException("macula.base.data.xstl.ioexception", e);
		} catch (TransformerException e) {
			log.error("", e);
			throw new MaculaCloudException("macula.base.data.xstl.transformerexception", e);
		}
	}

	@Override
	@Autowired
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
