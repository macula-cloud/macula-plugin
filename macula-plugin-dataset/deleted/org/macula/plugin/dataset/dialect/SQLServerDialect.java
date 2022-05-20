package org.macula.plugin.dataset.dialect;

import org.macula.plugin.core.utils.StringUtils;

/**
 * <p> <b>SQLServerDialect</b> 是SQLServer的方言实现. </p>
 */
public class SQLServerDialect extends AbstractDialect {

	@Override
	public String getLimitedString(String sql, int startPos, int size) {
		if (size < 0) {
			return sql;
		}
		return new StringBuffer(sql.length() + 8).append(sql)
				.insert(getAfterSelectInsertPoint(StringUtils.lowerCase(sql)), " top " + (startPos + size)).toString();
	}

	private int getAfterSelectInsertPoint(String sql) {
		return sql.startsWith("select distinct") ? 15 : 6;
	}

	@Override
	public int getStartPos(int startPos, int size) {
		return startPos;
	}

	@Override
	public boolean match(String product) {
		return product.indexOf("SQL Server") >= 0;
	}

	@Override
	public String getProcCallback(String sql, boolean hasReturn) {
		return "{ call " + sql + " }";
	}

}
