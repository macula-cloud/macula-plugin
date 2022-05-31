package org.macula.plugin.dataset.loader.dataparam;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.dataset.domain.DataParam;

import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * <p> <b>XmlDataParamLoaderImpl</b> 通过XML配置加载数据参数 </p>
 * 
 */
@Slf4j
@Component
public class XmlDataParamLoaderImpl implements DataParamLoader {

	private ClassPathXmlApplicationContext applicationContext;

	/**
	 * 从/data/modulename/*-dataparam.xml读取DataParam
	 * 
	 * @param dataParamCode
	 */
	@Override
	public DataParam loader(String dataParamCode) {
		if (applicationContext == null) {
			refresh();
		}
		if (applicationContext != null) {
			try {
				return (DataParam) applicationContext.getBean(dataParamCode);
			} catch (BeansException ex) {
				// ignore
			}
		}
		return null;
	}

	@Override
	public void refresh() {
		// 读取DataParam配置文件  
		try {
			applicationContext = new ClassPathXmlApplicationContext("classpath*:data/**/*-dataparam.xml");
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
