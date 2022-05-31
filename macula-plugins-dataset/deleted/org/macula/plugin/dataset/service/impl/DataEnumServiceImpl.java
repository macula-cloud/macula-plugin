package org.macula.plugin.dataset.service.impl;

import java.util.List;
import java.util.Locale;

import org.macula.plugin.dataset.domain.DataEnum;
import org.macula.plugin.dataset.repository.DataEnumRepository;
import org.macula.plugin.dataset.service.DataEnumService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * <b>DataEnumServiceImpl</b> 是BaseEnumService的实现类.
 * </p>
 */
@Service
public class DataEnumServiceImpl implements DataEnumService {

	@Autowired
	private DataEnumRepository dataEnumRepository;

	@Override
	@Transactional(readOnly = true)
	public List<? extends DataEnum> findEnabledEnums(String type, Locale locale) {
		Locale localex = locale == null ? Locale.getDefault() : locale;
		return dataEnumRepository.findByTypeAndLocaleAndEnabledOrderByOrderedAsc(type, localex.toString(), true);
	}

	@Override
	@Transactional(readOnly = true)
	public String getEnumCode2Value(String type, String code, Locale locale) {
		Locale localex = locale == null ? Locale.getDefault() : locale;
		DataEnum result = dataEnumRepository.getByTypeAndCodeAndLocaleAndEnabled(type, code, localex.toString(), true);
		return result == null ? null : result.getName();
	}

}
