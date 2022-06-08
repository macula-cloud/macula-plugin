package org.macula.plugin.dataset.dialect;

/**
 * <p>
 * <b>AbstractDialect</b> 是抽象的方言实现.
 * </p>
 */
public abstract class AbstractDialect implements JdbcDialect {

	// TODO : mysql 和 sqlserver在有order by语句时，是否能够正常允许尚且需要验证.
	@Override
	public String getCountString(String sql) {
		return "select count(*) from (" + sql + ") mycount";
	}
}
