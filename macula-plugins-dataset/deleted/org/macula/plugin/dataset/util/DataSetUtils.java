package org.macula.plugin.dataset.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.macula.cloud.api.context.CloudApplicationContext;
import org.macula.cloud.api.protocol.FieldOption;
import org.macula.cloud.api.utils.ConversionUtils;
import org.macula.plugin.core.utils.SecurityUtils;
import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.domain.DataArg;
import org.macula.plugin.dataset.domain.DataSet;
import org.macula.plugin.dataset.handle.DataHandlerChain;
import org.macula.plugin.dataset.handle.HandlerChainSerializer;
import org.macula.plugin.dataset.handle.impl.FreemarkerDataHandler;
import org.macula.plugin.dataset.handle.impl.QueryExecutorHandler;
import org.macula.plugin.dataset.handle.impl.QueryParserDataHandler;
import org.macula.plugin.dataset.handle.support.BeanPropertyRowMapper;
import org.macula.plugin.dataset.service.DataSetService;
import org.macula.plugin.dataset.value.impl.DataParamValueEntryResolver;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

/**
 * <p>
 * <b>DataSetUtils</b> 是SQL执行的助手.
 * </p>
 * 
 */
@Slf4j
public final class DataSetUtils {
	private DataSetUtils() {
		// Noops
	}

	private static final Map<Class<?>, RowMapper<?>> rowMapperCache = new ConcurrentHashMap<Class<?>, RowMapper<?>>();

	public static final class DataSetUtilsHolder {

		private static DataParamValueEntryResolver dataParamValueEntryResolver;
		private static DataSetService dataSetService;
		private static HandlerChainSerializer factory;

		private DataSetUtilsHolder() {
			// Noops
		}

		public static synchronized DataSetService getDataSetService() {
			if (dataSetService == null) {
				dataSetService = CloudApplicationContext.getBean(DataSetService.class);
			}
			return dataSetService;
		}

		public static synchronized HandlerChainSerializer getHandlerChainFactory() {
			if (factory == null) {
				factory = CloudApplicationContext.getBean(HandlerChainSerializer.class);
			}
			return factory;
		}

		/**
		 * @return the dataParamRepository
		 */
		public static synchronized DataParamValueEntryResolver getDataParamValueEntryResolver() {
			if (dataParamValueEntryResolver == null) {
				dataParamValueEntryResolver = CloudApplicationContext.getBean(DataParamValueEntryResolver.class);
			}
			return dataParamValueEntryResolver;
		}
	}

	// ------------------------------------------------------------------------------------------------------------------//
	/**
	 * 传入SQL语句与数据源执行SQL，SQL语句可以含有FreeMarker和Spring表达式
	 * 
	 * @param sql SQL语句
	 * @param dataSource 数据源
	 * @param userContext 上下文
	 * @param params 参数, JavaBean参数或者Map参数
	 * @param pageable 分页
	 * @return Page
	 */
	public static <T> Page<T> query(String sql, DataSource dataSource, Object userContext, Object params,
			Pageable pageable) {
		return query(sql, dataSource, userContext, params, pageable, null);
	}

	public static <T> Page<T> query(String sql, DataSource dataSource, Object userContext, Object params,
			Pageable pageable, Class<T> requiredType) {
		Map<String, Object> mappedParams = convertBean2Map(params);

		DataHandlerChain chain = new DataHandlerChain(CloudApplicationContext.getBean(FreemarkerDataHandler.class),
				CloudApplicationContext.getBean(QueryParserDataHandler.class),
				CloudApplicationContext.getBean(QueryExecutorHandler.class));
		if (null != mappedParams) {
			for (Entry<String, Object> entry : mappedParams.entrySet()) {
				chain.addInitialParameter(entry.getKey(), entry.getValue());
			}
		}

		return doChain(chain, sql, userContext, dataSource, pageable, requiredType);
	}

	// ------------------------------------------------------------------------------------------------------------------//

	/**
	 * 根据DataSet代码执行DataSet
	 * 
	 * @param code DataSet代码
	 * @param userContext 用户上下文
	 * @param params 参数,JavaBean或者Map
	 * @param pageable 分页
	 * @return Page
	 */
	public static <T> Page<T> query(String code, Object userContext, Object params, Pageable pageable) {
		return query(code, userContext, params, pageable, null);
	}

	public static <T> Page<T> query(String code, Object userContext, Object params, Pageable pageable,
			Class<T> requiredType) {
		DataSetService dataSetService = DataSetUtilsHolder.getDataSetService();
		DataSet dataSet = dataSetService.findByCode(code);
		return query(dataSet, userContext, params, pageable, requiredType);
	}

	// ------------------------------------------------------------------------------------------------------------------//

	/**
	 * 执行DataSet，返回执行结果
	 * 
	 * @param dataSet DataSet对象
	 * @param userContext 用户上下文
	 * @param params JavaBean类型的参数或者Map参数
	 * @param pageable 分页
	 * @return Page
	 */
	public static <T> Page<T> query(DataSet dataSet, Object userContext, Object params, Pageable pageable) {
		return query(dataSet, userContext, params, pageable, null);
	}

	public static <T> Page<T> query(DataSet dataSet, Object userContext, Object params, Pageable pageable,
			Class<T> requiredType) {
		Assert.notNull(dataSet, "DataSet should not be null!");
		String chainXml = dataSet.getHandlerChain();
		HandlerChainSerializer factory = DataSetUtilsHolder.getHandlerChainFactory();
		DataHandlerChain chain;
		try {
			chain = factory.deserialize(chainXml);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Deserialize chain xml occured error:", e);
			}
			throw new IllegalArgumentException(e);
		}
		if (chain == null || chain.getHandlers() == null || chain.getHandlers().length == 0) {
			chain = new DataHandlerChain(CloudApplicationContext.getBean(FreemarkerDataHandler.class),
					CloudApplicationContext.getBean(QueryParserDataHandler.class),
					CloudApplicationContext.getBean(QueryExecutorHandler.class));
		}

		Map<String, Object> mappedParams = convertBean2Map(params);

		List<? extends DataArg> args = dataSet.getDataArgs();
		Map<String, Object> extractParam = new HashMap<String, Object>();
		if (mappedParams != null) {
			extractParam.putAll(mappedParams);
		}
		if (args != null && !args.isEmpty()) {
			for (DataArg dataArg : args) {
				Class<?> typeClass = dataArg.getDataType().getTypeClass();
				String argName = dataArg.getName();
				Object paramValue = extractParam.get(argName);
				List<FieldOption> options = null;
				if (dataArg.getDataParam() != null) {
					options = createFieldOptions(dataArg.getDataParam().getCode(), userContext);
				}
				// 检查参数值是否在指定的DataParams的列表中
				if (paramValue instanceof List<?>) {
					ArrayList<Object> extractlyValue = new ArrayList<Object>();
					for (Object object : (List<?>) paramValue) {
						Object convertedValue = ConversionUtils.convertQuietly(object, typeClass);
						if (options == null || contains(convertedValue, options)) {
							extractlyValue.add(ConversionUtils.convertQuietly(object, typeClass));
						}
					}
					chain.addInitialParameter(argName, extractlyValue);
				} else {
					Object convertedValue = ConversionUtils.convertQuietly(paramValue, typeClass);
					if (options == null || contains(convertedValue, options)) {
						if (convertedValue instanceof Serializable) {
							chain.addInitialParameter(argName, convertedValue);
						} else {
							chain.addInitialParameter(argName, String.valueOf(convertedValue));
						}
					}
				}
			}
		}

		Pageable exactlyPageable = dataSet.isPagable() ? pageable : null;
		DataSource dataSource = DataSourceUtils.get(dataSet.getDataSource());
		String sql = dataSet.getExpressionText();

		DataSetNameHolder.set(dataSet.getCode());

		Page<T> page = doChain(chain, sql, userContext, dataSource, exactlyPageable, requiredType);

		DataSetNameHolder.remove();

		return page;
	}

	// ------------------------------------------------------------------------------------------------------------------//
	/**
	 * 传入SQL语句与数据源执行SQL，SQL语句可以含有FreeMarker和Spring表达式
	 * 
	 * @param sql SQL语句
	 * @param dataSource 数据源
	 * @param params 参数, JavaBean参数或者Map参数
	 * @param pageable 分页
	 * @return Page
	 */
	public static <T> Page<T> queryByDefaultContext(String sql, DataSource dataSource, Object params,
			Pageable pageable) {
		return query(sql, dataSource, SecurityUtils.getObject(), params, pageable);
	}

	public static <T> Page<T> queryByDefaultContext(String sql, DataSource dataSource, Object params, Pageable pageable,
			Class<T> requiredType) {
		return query(sql, dataSource, SecurityUtils.getObject(), params, pageable, requiredType);
	}

	// ------------------------------------------------------------------------------------------------------------------//

	/**
	 * 根据DataSet代码执行DataSet
	 * 
	 * @param code DataSet代码
	 * @param params 参数,JavaBean或者Map
	 * @param pageable 分页
	 * @return Page
	 */
	public static <T> Page<T> queryByDefaultContext(String code, Object params, Pageable pageable) {
		return query(code, SecurityUtils.getObject(), params, pageable);
	}

	public static <T> Page<T> queryByDefaultContext(String code, Object params, Pageable pageable,
			Class<T> requiredType) {
		return query(code, SecurityUtils.getObject(), params, pageable, requiredType);
	}

	// ------------------------------------------------------------------------------------------------------------------//

	/**
	 * 执行DataSet，返回执行结果
	 * 
	 * @param dataSet DataSet对象
	 * @param userContext 用户上下文
	 * @param params JavaBean类型的参数或者Map参数
	 * @param pageable 分页
	 * @return Page
	 */
	public static <T> Page<T> queryByDefaultContext(DataSet dataSet, Object params, Pageable pageable) {
		return query(dataSet, SecurityUtils.getObject(), params, pageable);
	}

	public static <T> Page<T> queryByDefaultContext(DataSet dataSet, Object params, Pageable pageable,
			Class<T> requiredType) {
		return query(dataSet, SecurityUtils.getObject(), params, pageable, requiredType);
	}

	// ------------------------------------------------------------------------------------------------------------------//

	/**
	 * 根据参数代码的枚举的Code获取具体的枚举值，这里可以在参数SQL语句中添加#(dataCode)#作为where条件，
	 * 也可以不添加where条件，程序会通过循环匹配所有枚举值的方式查找。参数SQL语句可以返回多列数据，其中
	 * 第一列和第二列作为Code和Label的数据，然后所有的数据会存放在{@link FieldOption}的extra属性中。
	 * 
	 * @param dataParamCode
	 * @param dataCode
	 * @param extraParams
	 * @param userContext
	 * @return {@link FieldOption}
	 */
	public static FieldOption createFieldOption(String dataParamCode, String dataCode, Map<String, Object> extraParams,
			Object userContext) {

		// 将DataCode传递给，用于直接翻译代码时的参数解析
		// 后端的SQL语句可以包含DataCode参数，也可以不包含DataCode参数
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataCode", dataCode);

		if (extraParams != null) {
			params.putAll(extraParams);
		}

		Object extraObject = userContext.addAdditionInfo(params);
		List<FieldOption> options = DataSetUtils.createFieldOptions(dataParamCode, extraObject);
		if (options.size() == 1) {
			// 返回单条记录，说明已经完全定位到代码对应的具体数据了
			return options.get(0);
		} else if (options.size() > 1) {
			// 根据DataCode逐个匹配
			for (FieldOption o : options) {
				if (dataCode.equals(o.getCode())) {
					return o;
				}
			}
		}
		return new FieldOption(dataCode, dataCode);
	}

	/**
	 * 获取参数情况
	 * 
	 * @param dataParamCode
	 * @param userContext
	 * @return List
	 */
	public static List<FieldOption> createFieldOptions(String dataParamCode, Object userContext) {
		List<FieldOption> fieldOptions = new ArrayList<FieldOption>();
		List<Map<String, Object>> paramValues = createMappedOptions(dataParamCode, userContext);
		for (Map<String, Object> map : paramValues) {

			FieldOption fieldOption = null;
			if (map.get(FieldOption.ID_PARAM) != null) {
				fieldOption = new FieldOption(map.get(FieldOption.CODE_PARAM).toString(),
						map.get(FieldOption.LABEL_PARAM).toString(), (map.get(FieldOption.ID_PARAM).toString()));
			} else {
				fieldOption = new FieldOption(map.get(FieldOption.CODE_PARAM),
						map.get(FieldOption.LABEL_PARAM).toString());
			}
			// Arron20151028 end
			fieldOption.getExtra().putAll(map);
			fieldOptions.add(fieldOption);
		}
		return fieldOptions;
	}

	/**
	 * 将系统参数作为集合返回，集合中为一个Map，Map中一定包含code和label信息.
	 * 
	 * @param dataParamCode
	 * @param userContext
	 * @return List
	 */
	public static List<Map<String, Object>> createMappedOptions(String dataParamCode, Object userContext) {
		Object resolvedValue = userContext.resolve(DataParamValueEntryResolver.PREFIX + dataParamCode);
		List<Map<String, Object>> paramValues = new ArrayList<Map<String, Object>>();
		if (resolvedValue != null) {
			if (resolvedValue instanceof List) {
				for (Object v : (List<?>) resolvedValue) {
					Map<String, Object> option = convertObject(v);
					if (option != null) {
						paramValues.add(option);
					}
				}
			} else {
				Map<String, Object> option = new HashMap<String, Object>();
				option.put(FieldOption.CODE_PARAM, resolvedValue);
				option.put(FieldOption.LABEL_PARAM, resolvedValue.toString());
				paramValues.add(option);
			}
		}
		return paramValues;
	}

	/**
	 * 将系统参数作为集合返回，集合中为一个Map，Map中一定包含code和label信息，可分页
	 * 
	 * @param dataParamCode
	 * @param userContext
	 * @param pageable
	 */
	public static Page<Map<String, Object>> createMappedOptions(String dataParamCode, Object userContext,
			Pageable pageable) {
		Page<Object> page = DataSetUtilsHolder.getDataParamValueEntryResolver().resolve(dataParamCode, userContext,
				pageable);
		List<Object> content = page.getContent();
		List<Map<String, Object>> converted = new ArrayList<Map<String, Object>>();
		for (Object v : content) {
			converted.add(convertObject(v));
		}
		return new PageImpl<Map<String, Object>>(converted, PageRequest.of(page.getNumber(), page.getSize()),
				page.getTotalElements());
	}

	public static boolean contains(Object object, List<FieldOption> options) {
		if (options != null && !options.isEmpty()) {
			for (FieldOption fieldOption : options) {
				if (fieldOption.getCode().equals(object)) {
					return true;
				}
			}
		}
		return false;
	}

	public static List<Object> getFieldOptionCodes(List<FieldOption> options) {
		List<Object> codes = new ArrayList<Object>();
		if (options != null && !options.isEmpty()) {
			for (FieldOption option : options) {
				codes.add(option.getCode());
			}
		}
		return codes;
	}

	private static Map<String, Object> convertObject(Object v) {
		if (v instanceof Object[]) {
			Object[] vv = (Object[]) v;
			Object key = vv[0];
			if (key != null) {
				String label = key.toString();
				if (vv.length > 1) {
					label = vv[1].toString();
				}
				Map<String, Object> option = new HashMap<String, Object>();
				option.put(FieldOption.CODE_PARAM, key);
				option.put(FieldOption.LABEL_PARAM, label);
				return (option);
			}
		} else if (v instanceof Map<?, ?>) {
			Map<?, ?> vv = (Map<?, ?>) v;
			int index = 0;
			Map.Entry<?, ?> codeEntry = null, labelEntry = null;
			Map<String, Object> option = new HashMap<String, Object>();
			for (Map.Entry<?, ?> entry : vv.entrySet()) {
				if (index == 0 && codeEntry == null) {
					codeEntry = entry;
				}
				if (index == 1 && labelEntry == null) {
					labelEntry = entry;
				}

				if (StringUtils.equalsIgnoreCase(FieldOption.CODE_PARAM, (String) entry.getKey())) {
					codeEntry = entry;
				}
				if (StringUtils.equalsIgnoreCase(FieldOption.LABEL_PARAM, (String) entry.getKey())) {
					labelEntry = entry;
				}
				option.put(StringUtils.lowerCase(entry.getKey().toString()), entry.getValue());
				index++;
			}
			if (codeEntry != null && codeEntry.getValue() != null) {
				option.put(FieldOption.CODE_PARAM, codeEntry.getValue());
			}
			if (labelEntry != null && labelEntry.getValue() != null) {
				option.put(FieldOption.LABEL_PARAM, labelEntry.getValue());
			}
			if (option.containsKey(FieldOption.CODE_PARAM) && !option.containsKey(FieldOption.LABEL_PARAM)) {
				option.put(FieldOption.LABEL_PARAM, option.get(FieldOption.CODE_PARAM).toString());
			}
			if (option.get(FieldOption.CODE_PARAM) != null) {
				return (option);
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	/**
	 * 转换bean为Map，如果已经是Map则不用转换，这样可以支持JavaBean作为参数
	 * 
	 * @param bean
	 */
	private static Map<String, Object> convertBean2Map(Object bean) {

		if (bean == null) {
			return null;
		}

		if (bean instanceof Map<?, ?>) {
			return (Map<String, Object>) bean;
		}

		Map<String, Object> mapParams = new HashMap<String, Object>();

		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		PropertyDescriptor[] props = beanWrapper.getPropertyDescriptors();
		for (PropertyDescriptor pd : props) {
			if (beanWrapper.isReadableProperty(pd.getName())) {
				String paramName = pd.getName();
				mapParams.put(paramName, beanWrapper.getPropertyValue(paramName));
			}
		}

		return mapParams;
	}

	@SuppressWarnings("unchecked")
	private static <T> Page<T> doChain(DataHandlerChain chain, String sql, Object userContext, DataSource dataSource,
			Pageable pageable, Class<T> requiredType) {

		if (requiredType != null && !Map.class.isAssignableFrom(requiredType)) {
			RowMapper<?> rowMapper = rowMapperCache.get(requiredType);
			if (rowMapper == null) {
				rowMapper = new BeanPropertyRowMapper<T>(requiredType);
				rowMapperCache.put(requiredType, rowMapper);
			}
			chain.addInitialParameter(QueryExecutorHandler.ROW_MAPPER, rowMapper);
		}

		return (Page<T>) chain.handle(sql, userContext, dataSource, pageable);
	}
}
