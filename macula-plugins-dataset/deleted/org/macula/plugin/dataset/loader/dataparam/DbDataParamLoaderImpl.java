package org.macula.plugin.dataset.loader.dataparam;

import org.macula.plugin.dataset.domain.DataParam;
import org.macula.plugin.dataset.repository.DataParamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <b>DbDataParamLoaderImpl</b> 从数据库中加载数据参数
 * </p>
 *
 */
@Component
public class DbDataParamLoaderImpl implements DataParamLoader {

	@Autowired
	private DataParamRepository dataParamRepository;

	@Override
	public DataParam loader(String code) {
		return dataParamRepository.findByCode(code);
	}

	@Override
	public void refresh() {

	}

	@Override
	public int getOrder() {
		return 100;
	}
}
