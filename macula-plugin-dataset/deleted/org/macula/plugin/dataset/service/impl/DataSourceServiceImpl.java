package org.macula.plugin.dataset.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.macula.plugin.dataset.domain.DataSource;
import org.macula.plugin.dataset.event.DataSourceChangedEvent;
import org.macula.plugin.dataset.loader.datasource.DataSourceLoader;
import org.macula.plugin.dataset.service.DataSourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <b>DataSourceServiceImpl</b> DataSource的提供者
 * </p>
 */
@Component
public class DataSourceServiceImpl implements DataSourceService, ApplicationListener<ApplicationEvent> {

	private Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();

	@Autowired
	private List<DataSourceLoader> loaders;

	@PostConstruct
	protected void initial() {
		if (loaders != null) {
			OrderComparator.sort(loaders);
		}
	}

	@Override
	public DataSource findByCode(String dataSourceCode) {
		DataSource dataSource = dataSourceMap.get(dataSourceCode);
		if (dataSource == null) {
			dataSource = findDsByCode(dataSourceCode);
			if (dataSource != null) {
				dataSourceMap.put(dataSourceCode, dataSource);
			}
		}
		return dataSource;
	}

	private DataSource findDsByCode(String dataSourceCode) {
		for (DataSourceLoader loader : loaders) {
			DataSource dataSource = loader.loader(dataSourceCode);
			if (dataSource != null) {
				return dataSource;
			}
		}
		return null;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// 自己或者关联的数据变化，则需要重新加载
		if (event instanceof DataSourceChangedEvent) {
			dataSourceMap.clear();
		}
	}

}
