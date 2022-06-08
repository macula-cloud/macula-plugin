package org.macula.plugin.dataset.handle.support;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.macula.cloud.api.protocol.FieldOption;
import org.macula.plugin.dataset.query.impl.DataParamTokenHandler;
import org.macula.plugin.dataset.util.DataSetUtils;

import org.springframework.beans.BeanWrapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * <p>
 * <b>BeanPropertyRowMapper</b> JdbcTemplate的RowMapper，按照列名生成到Bean中，如果列名中有#P(column$$data_param_code)#<br/>
 * 则会产生一个 propertyLabel的列，用于该列参数值的翻译，具体可以参考{@link DataParamTokenHandler}<br/>
 * 新的参数值属性与原属性保持一致，后缀一定是<strong>Label</strong>
 * </p>
 *
 */
public class BeanPropertyRowMapper<T> extends org.springframework.jdbc.core.BeanPropertyRowMapper<T> {
	private Map<String, Object> extractParams;
	private Object userContext;
	private BeanWrapper beanWrapper;

	public BeanPropertyRowMapper(Class<T> mappedClass) {
		super(mappedClass);
	}

	public void initContext(Map<String, Object> extractParams, Object userContext) {
		this.extractParams = extractParams;
		this.userContext = userContext;
	}

	protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
		Object value = super.getColumnValue(rs, index, pd);

		// 如果该字段是一个参数类型需要翻译的字段，则翻译它
		ResultSetMetaData rsmd = rs.getMetaData();
		String column = JdbcUtils.lookupColumnName(rsmd, index);
		String labelKey = column + "_" + DataParamTokenHandler.DATA_PARAM_KEY_SUFFIX;
		String labelKeyUpper = labelKey.toUpperCase();
		if (value != null && extractParams != null && extractParams.containsKey(labelKeyUpper)) {
			FieldOption option = DataSetUtils.createFieldOption((String) extractParams.get(labelKeyUpper),
					value.toString(), null, userContext);
			if (option != null) {
				beanWrapper.setPropertyValue(pd.getName() + DataParamTokenHandler.DATA_PARAM_KEY_SUFFIX,
						option.getLabel());
			}
		}

		return value;
	}

	protected void initBeanWrapper(BeanWrapper bw) {
		this.beanWrapper = bw;
	}
}
