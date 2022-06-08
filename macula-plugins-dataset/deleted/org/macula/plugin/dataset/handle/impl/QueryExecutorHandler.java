package org.macula.plugin.dataset.handle.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.macula.plugin.dataset.dialect.JdbcDialect;
import org.macula.plugin.dataset.handle.support.BeanPropertyRowMapper;
import org.macula.plugin.dataset.handle.support.ColumnMapRowMapper;
import org.macula.plugin.dataset.util.DataSourceUtils;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <b>QueryExecutorHandler</b> 是执行查询的转换.
 * 通过在参数中设置QueryExecutorHandler.ROW_MAPPER指定查询结果转出的具体类型，默认是{@link ColumnMapRowMapper}<br/>
 * 通过在参数中谁知QueryExecutorHandler.KEY_UPPERCASE指定查询结果的Map KEY转换为大写
 * </p>
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class QueryExecutorHandler extends AbstractDataHandler {

	private static final String NAME = "QueryExecutorHandler";

	public static final String AUTO_FIX_PAGE_NUM = "-auto_fix_page_num_";

	public static final String ROW_MAPPER = "-custom-row-mapper-";

	public static final String KEY_UPPERCASE = "-key_$$_uppercase-";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Class<?> getInputClass() {
		return String.class;
	}

	@Override
	public Class<?> getOutputClass() {
		return Page.class;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Page<?> handleInternal(Object data, Object userContext) {
		String sql = data.toString();
		if (log.isDebugEnabled()) {
			log.debug("Origin SQL:" + sql);
		}
		DataSource ds = (DataSource) getDataSource();
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(ds);

		// 是否有指定的ROW_MAPPER，没有则使用默认的ColumnMapRowMapper
		RowMapper<?> rowMapper = (RowMapper<?>) getExtractParams().get(ROW_MAPPER);
		if (rowMapper == null) {
			rowMapper = new ColumnMapRowMapper(getExtractParams(), userContext);
		} else if (rowMapper instanceof BeanPropertyRowMapper) {
			((BeanPropertyRowMapper<?>) rowMapper).initContext(getExtractParams(), userContext);
		}

		if (isPageable()) {
			JdbcDialect dialect = DataSourceUtils.getDialect(ds);
			String countSQL = dialect.getCountString(sql);
			int total = jdbcTemplate.queryForObject(countSQL, getExtractParams(), Integer.class);

			List<Object> content = new ArrayList<Object>();

			// 如果给定的页码值是Integer.Max_VALUE，则认为是不需要查内容
			if (getPageable().getPageNumber() < Integer.MAX_VALUE) {
				int startPos = getStartPos(getPageable());
				int pageSize = getPageSize(getPageable());
				Boolean autoFixPageNum = (Boolean) getExtractParams().get(AUTO_FIX_PAGE_NUM);
				if (autoFixPageNum != null && autoFixPageNum && startPos > total) {
					startPos = 0;
					setPageable(PageRequest.of(0, pageSize));
				}
				if (total > 0) {
					String limitedSQL = dialect.getLimitedString(sql, startPos, pageSize);
					if (log.isDebugEnabled()) {
						log.debug("Limit SQL:" + limitedSQL);
					}

					content = (List<Object>) jdbcTemplate.query(limitedSQL, getExtractParams(), rowMapper);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("Count SQL:" + countSQL);
			}

			return new PageImpl<Object>(content, getPageable(), total);
		}

		List<Object> content = (List<Object>) jdbcTemplate.query(sql, getExtractParams(), rowMapper);
		return new PageImpl<Object>(content, PageRequest.of(0, Integer.MAX_VALUE), content.size());
	}

	@Override
	public void initialize(Properties properties) {
		// nothing
	}

	@Override
	public Properties getProperties() {
		return null;
	}

	private int getStartPos(Pageable pageable) {
		return pageable.getPageNumber() * pageable.getPageSize();
	}

	private int getPageSize(Pageable pageable) {
		return pageable.getPageSize();
	}
}
