package org.macula.plugin.dataset.loader.dataset;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.dataset.domain.DataSet;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p> <b>XmlDataSetLoaderImpl</b> 从XML中加载DataSet </p>
 */
@Slf4j
@Component
public class XmlDataSetLoaderImpl implements DataSetLoader {

	private ClassPathXmlApplicationContext applicationContext;

	/**
	 * @param dataSetCode
	 */
	@Override
	public DataSet loader(String dataSetCode) {
		if (applicationContext == null) {
			refresh();
		}
		if (applicationContext != null) {
			try {
				return (DataSet) applicationContext.getBean(dataSetCode);
			} catch (BeansException e) {
				//IGNORE
			}
		}
		return null;
	}

	@Override
	public void refresh() {
		//从/data/modulename/*-dataset.xml读取DataSet
		try {
			applicationContext = new ClassPathXmlApplicationContext("classpath*:data/**/*-dataset.xml");
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
