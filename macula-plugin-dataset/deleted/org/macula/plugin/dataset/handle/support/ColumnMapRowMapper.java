package org.macula.plugin.dataset.handle.support;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.macula.cloud.api.protocol.FieldOption;
import org.macula.plugin.dataset.handle.impl.QueryExecutorHandler;
import org.macula.plugin.dataset.query.impl.DataParamTokenHandler;
import org.macula.plugin.dataset.util.DataSetUtils;

import org.springframework.jdbc.support.JdbcUtils;

/**
 * <p>
 * <b>ColumnMapRowMapper</b> JdbcTemplate的RowMapper，按照列名生成到Map中，如果列名中有#P(column$$data_param_code)#<br/>
 * 则会产生一个 column_Label的列，用于该列参数值的翻译，具体可以参考{@link DataParamTokenHandler}<br/>
 * 注意列的大小写，列的大小写完全和原始的column保持一致，后缀一定是<strong>_Label</strong>
 * </p>
 */
public class ColumnMapRowMapper extends org.springframework.jdbc.core.ColumnMapRowMapper {

	private Map<String, Object> extractParams;
	private Object userContext;

	public ColumnMapRowMapper(Map<String, Object> map, Object userContext) {
		this.extractParams = map;
		this.userContext = userContext;
	}

	@Override
	protected String getColumnKey(String columnName) {
		// 判断KEY是否需要转换大写(oracle默认都是返回大写KEY，而mysql则返回指定的key
		// 所以finder做了统一配置为大写KEY后，如果不进行大小写转换则很难适应两个数据库)
		Object _keyUppercase = extractParams.get(QueryExecutorHandler.KEY_UPPERCASE);
		if (_keyUppercase != null && "true".equals(_keyUppercase.toString())) {
			return super.getColumnKey(columnName).toUpperCase();
		}
		return super.getColumnKey(columnName);
	}

	@Override
	public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Map<String, Object> mapOfColValues = createColumnMap(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			Object obj = getColumnValue(rs, i);
			mapOfColValues.put(key, obj);

			// 如果该字段是一个参数类型需要翻译的字段，则翻译它
			String labelKey = key + "_" + DataParamTokenHandler.DATA_PARAM_KEY_SUFFIX;
			String labelKeyUpper = labelKey.toUpperCase();
			if (obj != null && extractParams.containsKey(labelKeyUpper)) {
				FieldOption option = DataSetUtils.createFieldOption((String) extractParams.get(labelKeyUpper),
						obj.toString(), null, userContext);
				if (option != null) {
					mapOfColValues.put(labelKey, option.getLabel());
				}
			}
		}
		return mapOfColValues;
	}
}
