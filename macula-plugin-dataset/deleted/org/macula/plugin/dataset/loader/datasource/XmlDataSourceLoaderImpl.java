package org.macula.plugin.dataset.loader.datasource;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.dataset.domain.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p> <b>XmlDataSourceLoaderImpl</b> 从XML中加载DataSource </p>
 */
@Slf4j
@Component
public class XmlDataSourceLoaderImpl implements DataSourceLoader {

	private ClassPathXmlApplicationContext applicationContext;

	/**
	 * 从/data/modulename/*-datasource.xml读取DataSource
	 * 
	 * @param dataSourceCode
	 */
	@Override
	public DataSource loader(String dataSourceCode) {
		if (applicationContext == null) {
			refresh();
		}
		if (applicationContext != null) {
			try {
				return (DataSource) applicationContext.getBean(dataSourceCode);
			} catch (BeansException ex) {
				// ignore
			}
		}
		return null;
	}

	@Override
	public void refresh() {
		// 读取DataSource配置文件 
		try {
			applicationContext = new ClassPathXmlApplicationContext("classpath*:data/**/*-datasource.xml");
		} catch (BeansException ex) {
			if (!(ex.getCause() instanceof IOException)) {
				log.error(ex.getMessage());
			}
		}
	}

	@Override
	public int getOrder() {
		return 200;
	}
}
