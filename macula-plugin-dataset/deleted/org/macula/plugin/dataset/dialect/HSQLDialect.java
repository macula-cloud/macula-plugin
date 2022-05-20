package org.macula.plugin.dataset.dialect;

/**
 * <p> <b>HSQLDialect</b> 是HSQLDB的方言实现. </p>
 */
public class HSQLDialect extends AbstractDialect {

	@Override
	public boolean match(String product) {
		return product.indexOf("HSQL") >= 0;
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
		throw new UnsupportedOperationException("HSQL not support.");
	}

}
