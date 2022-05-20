package org.macula.plugin.datalog.handler;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.macula.plugin.datalog.event.EventContext;
import org.macula.plugin.datalog.meta.MetadataReader;
import org.macula.plugin.datalog.util.DruidAnalysisUtils;

public abstract class AbstractMappedAuditHandler implements MappedAuditHandler {

	private Connection connection;

	private String sql;

	private Set<String> tables;

	private Map<String, String> aliasToTableMap;

	private Map<String, String> tableToAliasMap;

	private SQLStatement sqlStatement;

	protected MetadataReader metadataReader;
	protected EventContext eventContext;

	protected abstract SQLStatement parseSQLStatement(SQLStatementParser statementParser);

	protected AbstractMappedAuditHandler(Connection connection, String sql, MetadataReader metadataReader) {
		this.connection = connection;
		this.sql = sql;
		this.metadataReader = metadataReader;
		this.sqlStatement = parseSQLStatement(getSQLStatementParser(sql));
		SQLTableSource sqlTableSource = DruidAnalysisUtils.getMajorTableSource(sqlStatement);
		if (sqlTableSource != null) {
			aliasToTableMap = DruidAnalysisUtils.buildAliasToTableMap(sqlTableSource);
			tableToAliasMap = DruidAnalysisUtils.reverseKeyAndValueOfMap(aliasToTableMap);
			tables = tableToAliasMap.keySet();
		}
	}

	protected SQLStatementParser getSQLStatementParser(String sql) {
		return new SQLStatementParser(sql);
	}

	protected Connection getConnection() {
		return connection;
	}

	protected String getSql() {
		return sql;
	}

	protected Set<String> getTables() {
		return tables;
	}

	protected Map<String, String> getAliasToTableMap() {
		return aliasToTableMap;
	}

	protected Map<String, String> getTableToAliasMap() {
		return tableToAliasMap;
	}

	protected SQLStatement getSqlStatement() {
		return sqlStatement;
	}

	/**
	 * @param eventContext the eventContext to set
	 */
	@Override
	public void setEventContext(EventContext eventContext) {
		this.eventContext = eventContext;
	}

}