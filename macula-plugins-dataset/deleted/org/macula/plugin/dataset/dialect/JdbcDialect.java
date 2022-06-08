package org.macula.plugin.dataset.dialect;

/**
 * <p>
 * <b>JdbcDialect</b> 是数据库方言.
 * </p>
 */
public interface JdbcDialect {

	/**
	 * 是否匹配
	 * 
	 * @param product
	 * @return boolean
	 */
	boolean match(String product);

	/**
	 * 创建分页语句
	 * 
	 * @param sql
	 * @param startPos
	 * @param size
	 * @return limitedString
	 */
	String getLimitedString(String sql, int startPos, int size);

	/**
	 * 获取获得总数的sql语句
	 * 
	 * @param sql
	 * @return countSql
	 */

	String getCountString(String sql);

	/**
	 * 获取开始位置
	 * 
	 * @param startPos
	 * @return new startPos
	 */
	int getStartPos(int startPos, int size);

	/**
	 * 获取存储过程方法
	 * 
	 * @param sql
	 * @param hasReturn 是否有返回值
	 */
	String getProcCallback(String sql, boolean hasReturn);
}
