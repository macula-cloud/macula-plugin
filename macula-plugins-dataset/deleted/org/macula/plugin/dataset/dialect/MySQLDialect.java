package org.macula.plugin.dataset.dialect;

/**
 * <p> <b>MySQLDialect</b> 是MySQL的方言. </p>
 */
public class MySQLDialect extends AbstractDialect {

	@Override
	public boolean match(String product) {
		return product.indexOf("MySQL") >= 0;
	}

	@Override
	public String getLimitedString(String sql, int startPos, int size) {
		if (size < 0) {
			return sql;
		}
		return new StringBuffer(sql.length() + 20).append(sql).append(" limit " + startPos + "," + size).toString();
	}

	@Override
	public int getStartPos(int startPos, int size) {
		return size > 0 ? 0 : startPos;
	}

	@Override
	public String getProcCallback(String sql, boolean hasReturn) {
		throw new UnsupportedOperationException("MySQL not support.");
	}

}
