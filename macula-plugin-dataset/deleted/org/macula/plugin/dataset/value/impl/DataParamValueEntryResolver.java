package org.macula.plugin.dataset.value.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.macula.cloud.api.context.CloudApplicationContext;
import org.macula.cloud.api.protocol.FieldOption;
import org.macula.cloud.api.utils.ConversionUtils;
import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.domain.DataParam;
import org.macula.plugin.dataset.domain.DataSource;
import org.macula.plugin.dataset.event.DataParamChangedEvent;
import org.macula.plugin.dataset.handle.DataHandlerChain;
import org.macula.plugin.dataset.handle.impl.FreemarkerDataHandler;
import org.macula.plugin.dataset.handle.impl.QueryExecutorHandler;
import org.macula.plugin.dataset.handle.impl.QueryParserDataHandler;
import org.macula.plugin.dataset.handle.impl.StaticOptionsDataHandler;
import org.macula.plugin.dataset.service.DataParamService;
import org.macula.plugin.dataset.util.DataSourceUtils;
import org.macula.plugin.dataset.value.ValueEntry;
import org.macula.plugin.dataset.value.ValueEntryResolver;
import org.macula.plugin.dataset.value.ValueEntryStorage;
import org.macula.plugin.dataset.value.scope.ValueScope;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * <p> <b>DataParamValueEntryResolver</b> 是基于系统参数表的参数解析. </p>
 */
@Component
public class DataParamValueEntryResolver implements ValueEntryResolver, ApplicationListener<DataParamChangedEvent> {

	@Autowired
	private DataParamService dataParamService;
	@Autowired
	private ValueEntryStorage valueEntryStorage;

	private int order = DEFAULT_ORDER;
	private final Map<String, Boolean> resolveCache = new ConcurrentHashMap<String, Boolean>();

	public static final String PREFIX = "DataParam$";
	public static final int DEFAULT_ORDER = 100;

	@Override
	public void onApplicationEvent(DataParamChangedEvent event) {
		if (event != null) {
			resolveCache.remove(PREFIX + event.getSource());
			valueEntryStorage.remove(PREFIX + event.getSource());
		}
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public boolean support(String key) {
		if (StringUtils.isBlank(key) || !key.startsWith(PREFIX)) {
			return false;
		}
		if (resolveCache.containsKey(key)) {
			return resolveCache.get(key);
		}
		boolean value = dataParamService.findByCode(key.substring(PREFIX.length())) != null;
		resolveCache.put(key, value);
		return value;
	}

	@Override
	public ValueEntry resolve(String attribute, Object userContext) {

		if (!support(attribute)) {
			return null;
		}

		String code = attribute.substring(PREFIX.length());
		DataParam dataParam = dataParamService.findByCode(code);

		if (dataParam == null) {
			return null;
		}

		ValueScope valueScope = dataParam.getValueScope();
		ValueEntry valueEntry = valueEntryStorage.retrieve(attribute, valueScope);
		if (valueEntry != null) {
			return valueEntry;
		}
		List<?> resolvedValue = resolve(code, userContext, null).getContent();
		valueEntry = new ValueEntry(attribute, (Serializable) resolvedValue, valueScope);
		return valueEntryStorage.store(valueEntry);
	}

	@SuppressWarnings("unchecked")
	public <T> Page<T> resolve(String code, Object userContext, Pageable pageable) {
		DataParam dataParam = dataParamService.findByCode(code);
		String originTetxt = dataParam.getValue();
		DataSource dataSource = dataParam.getDataSource();
		Page<T> resolvedValue = null;
		if (dataSource == null) {
			DataHandlerChain dataHandleChain = new DataHandlerChain(
					CloudApplicationContext.getBean(StaticOptionsDataHandler.class));
			dataHandleChain.addInitialParameter(StaticOptionsDataHandler.TARGET_CLASS,
					dataParam.getDataType().getTypeClass());
			List<T> content = (List<T>) dataHandleChain.handle(originTetxt, userContext, null, null);
			resolvedValue = new PageImpl<T>(content, pageable == null ? PageRequest.of(0, Integer.MAX_VALUE) : pageable,
					content.size());
		} else {
			DataHandlerChain dataHandleChain = new DataHandlerChain(
					CloudApplicationContext.getBean(FreemarkerDataHandler.class),
					CloudApplicationContext.getBean(QueryParserDataHandler.class),
					CloudApplicationContext.getBean(QueryExecutorHandler.class));
			resolvedValue = (Page<T>) dataHandleChain.handle(originTetxt, userContext, DataSourceUtils.get(dataSource),
					pageable);
			if (resolvedValue != null) {
				List<?> content = resolvedValue.getContent();
				for (Object object : content) {
					if (object instanceof Map) {
						Map<?, Object> contentMap = (Map<?, Object>) object;
						Map.Entry<?, Object> convertEntry = null;
						int index = 0;
						for (Map.Entry<?, Object> entry : contentMap.entrySet()) {
							if (index == 0) {
								convertEntry = entry;
							}
							if (StringUtils.equalsIgnoreCase(FieldOption.CODE_PARAM, (String) entry.getKey())) {
								convertEntry = entry;
								break;
							}
							index++;
						}
						if (convertEntry != null) {
							Object convertedValue = ConversionUtils.convertQuietly(convertEntry.getValue(),
									dataParam.getDataType().getTypeClass());
							convertEntry.setValue(convertedValue);
						}
					}
				}
			}
		}
		return resolvedValue;
	}

	@Override
	public int compareTo(ValueEntryResolver other) {
		return this.getOrder() - other.getOrder();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/**
	 * @param order
	 *            the order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}

}
