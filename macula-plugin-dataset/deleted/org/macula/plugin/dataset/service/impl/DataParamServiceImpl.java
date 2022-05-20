package org.macula.plugin.dataset.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.macula.cloud.api.exception.MaculaCloudException;
import org.macula.plugin.dataset.domain.DataParam;
import org.macula.plugin.dataset.event.DataParamChangedEvent;
import org.macula.plugin.dataset.event.DataSourceChangedEvent;
import org.macula.plugin.dataset.loader.dataparam.DataParamLoader;
import org.macula.plugin.dataset.service.DataParamService;
import org.macula.plugin.dataset.util.QueryReferenceUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Component;

/**
 * <p> <b>DataParamServiceImpl</b> 数据参数加载Service </p>
 * 
 */
@Component
public class DataParamServiceImpl implements DataParamService, ApplicationListener<ApplicationEvent> {

	private Map<String, DataParam> dataParamMap = new HashMap<String, DataParam>();

	@Autowired
	private List<DataParamLoader> loaders;

	@PostConstruct
	protected void initial() {
		if (loaders != null) {
			OrderComparator.sort(loaders);
		}
	}

	@Override
	public DataParam findByCode(String dataParamCode) {
		DataParam dataParam = dataParamMap.get(dataParamCode);
		if (dataParam == null) {
			dataParam = findByCode(dataParamCode, new ArrayList<DataParam>());
			if (dataParam != null) {
				dataParamMap.put(dataParamCode, dataParam);
			}
		}
		return dataParam;
	}

	private DataParam findByCode(String dataParamCode, List<DataParam> foundReferences) {
		for (DataParamLoader loader : loaders) {
			DataParam dataParam = loader.loader(dataParamCode);
			if (dataParam != null) {
				if (foundReferences.contains(dataParam)) {
					continue;
				}
				String value = dataParam.getValue();
				if (!QueryReferenceUtils.isReferenceValue(value)) {
					return dataParam;
				}
				foundReferences.add(dataParam);
				String refCode = QueryReferenceUtils.getReferenceCode(value);
				DataParam ref = findByCode(refCode, foundReferences);
				if (ref != null) {
					DataParam refDataParam = new DataParam();
					BeanUtils.copyProperties(dataParam, refDataParam);
					refDataParam.setValue(ref.getValue());
					return refDataParam;
				}
				throw new MaculaCloudException("Reference DataParam: " + refCode + " not existed...");
			}
		}
		return null;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// 主要针对XML, parse之后会缓存，所以如果数据源变了要refresh
		if (event instanceof DataSourceChangedEvent) {
			for (DataParamLoader loader : loaders) {
				loader.refresh();
			}
		}

		// 自己或者关联的数据变化，则需要重新加载
		if (event instanceof DataParamChangedEvent || event instanceof DataSourceChangedEvent) {
			dataParamMap.clear();
		}
	}
}
