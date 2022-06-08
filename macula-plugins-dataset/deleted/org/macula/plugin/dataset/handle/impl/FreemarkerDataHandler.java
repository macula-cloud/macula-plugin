package org.macula.plugin.dataset.handle.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.macula.cloud.api.exception.MaculaCloudException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

/**
 * <p> <b>FreemarkerDataHandler</b> 是Freemarker的数据处理器. </p>
 * 
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FreemarkerDataHandler extends StringDataHandler {

	@Autowired
	private FreeMarkerConfigurer freemarkerConfigurer;

	public static final String NAME = "FreemarkerDataHandler";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String handleInternal(Object data, Object userContext) {
		try {
			Template t = new Template(UUID.randomUUID().toString(), new StringReader(data.toString()),
					freemarkerConfigurer.getConfiguration());
			StringWriter sw = new StringWriter();
			t.setObjectWrapper(CustomFreemarkerBeanWrapper.getWrapperInstance());
			t.process(userContext, sw);
			return sw.toString();
		} catch (IOException e) {
			log.error("Process template error: ", e);
			throw new MaculaCloudException("macula.base.data.freemarker.ioexception", e);
		} catch (TemplateException e) {
			log.error("Process template error: ", e);
			throw new MaculaCloudException("macula.base.data.freemarker.templateexception", e);
		}
	}

	/**
	 * @param freemarkerConfigurer
	 *            the freemarkerConfigurer to set
	 */
	public void setFreemarkerConfigurer(FreeMarkerConfigurer freemarkerConfigurer) {
		this.freemarkerConfigurer = freemarkerConfigurer;
	}

}
