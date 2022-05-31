package org.macula.plugin.dataset.dialect;

/**
 * <p> <b>OracleDialect</b> 是Oracle的方言实现. </p>
 */
public class OracleDialect extends AbstractDialect {

	@Override
	public String getLimitedString(String sql, int startPos, int size) {
		// 不分批查询数据就直接返回SQL
		if (size < 0) {
			return sql;
		}

		// 加入分批查询语句
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		pagingSelect.append(sql);
		pagingSelect.append(" ) row_ where rownum <= " + (startPos + size) + ") where rownum_ > " + startPos);
		return pagingSelect.toString();
	}

	@Override
	public int getStartPos(int startPos, int size) {
		return size > 0 ? 0 : startPos;
	}

	@Override
	public boolean match(String product) {
		return product.indexOf("Oracle") >= 0;
	}

	@Override
	public String getProcCallback(String sql, boolean hasReturn) {
		return hasReturn ? "{ ? = call " + sql + "}" : "{call " + sql + " }";
	}

}
