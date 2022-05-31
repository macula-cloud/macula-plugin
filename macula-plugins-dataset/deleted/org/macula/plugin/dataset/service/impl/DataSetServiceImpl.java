package org.macula.plugin.dataset.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.macula.cloud.api.exception.MaculaCloudException;
import org.macula.plugin.dataset.domain.DataSet;
import org.macula.plugin.dataset.event.DataParamChangedEvent;
import org.macula.plugin.dataset.event.DataSetChangedEvent;
import org.macula.plugin.dataset.event.DataSourceChangedEvent;
import org.macula.plugin.dataset.loader.dataset.DataSetLoader;
import org.macula.plugin.dataset.service.DataSetService;
import org.macula.plugin.dataset.util.QueryReferenceUtils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.OrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p> <b>DataSetServiceImpl</b> DataSet的提供者 </p>
 */
@Service
public class DataSetServiceImpl implements DataSetService, ApplicationListener<ApplicationEvent> {

	private Map<String, DataSet> dataSetMap = new HashMap<String, DataSet>();

	@Autowired
	private List<DataSetLoader> loaders;

	@PostConstruct
	protected void initial() {
		if (loaders != null) {
			OrderComparator.sort(loaders);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DataSet findByCode(String dataSetCode) {
		DataSet dataSet = dataSetMap.get(dataSetCode);
		if (dataSet == null) {
			dataSet = findByCode(dataSetCode, new ArrayList<DataSet>());
			if (dataSet != null) {
				dataSetMap.put(dataSetCode, dataSet);
			}
		}
		return dataSet;
	}

	private DataSet findByCode(String dataSetCode, List<DataSet> foundDataSets) {
		for (DataSetLoader loader : loaders) {
			DataSet dataSet = loader.loader(dataSetCode);
			// lazy load
			if (dataSet != null) {
				if (dataSet.getDataArgs() != null) {
					dataSet.getDataArgs().size();
				}
				dataSet.getDataSource();
				if (foundDataSets.contains(dataSet)) {
					continue;
				}
				String value = dataSet.getExpressionText();
				if (!QueryReferenceUtils.isReferenceValue(value)) {
					return dataSet;
				}
				foundDataSets.add(dataSet);
				String refCode = QueryReferenceUtils.getReferenceCode(value);
				DataSet ref = findByCode(refCode, foundDataSets);
				if (ref != null) {
					DataSet refDataSet = new DataSet();
					BeanUtils.copyProperties(dataSet, refDataSet);
					refDataSet.setExpressionText(ref.getExpressionText());
					return refDataSet;
				}
				throw new MaculaCloudException("Reference DataSet: " + refCode + " not existed...");
			}
		}
		return null;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// 由于DataSet关联了DataSource和DataParam，主要是针对XML数据集
		if (event instanceof DataSourceChangedEvent || event instanceof DataParamChangedEvent) {
			for (DataSetLoader loader : loaders) {
				loader.refresh();
			}
		}

		// 关联数据变化，清除缓存
		if (event instanceof DataSetChangedEvent
			|| event instanceof DataSourceChangedEvent
			|| event instanceof DataParamChangedEvent) {
			dataSetMap.clear();
		}
	}
}
