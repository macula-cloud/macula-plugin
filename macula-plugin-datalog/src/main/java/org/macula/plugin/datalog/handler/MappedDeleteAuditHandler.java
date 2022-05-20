package org.macula.plugin.datalog.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.macula.plugin.datalog.meta.MetadataReader;
import org.macula.plugin.datalog.util.DruidAnalysisUtils;
import org.macula.plugin.datalog.util.OperationUtils;
import org.macula.plugin.datalog.util.SQLParseUtils;

import org.springframework.util.StringUtils;

public class MappedDeleteAuditHandler extends AbstractMappedAuditHandler {

	public MappedDeleteAuditHandler(Connection connection, String sql, MetadataReader metadataReader) {
		super(connection, sql, metadataReader);
	}

	@Override
	protected SQLStatement parseSQLStatement(SQLStatementParser statementParser) {
		return statementParser.parseDeleteStatement();
	}

	@Override
	@SneakyThrows
	public void beforeHandle() {
		if (getSqlStatement() instanceof SQLDeleteStatement) {
			SQLDeleteStatement deleteStatement = (SQLDeleteStatement) getSqlStatement();
			SQLTableSource affectTableSource = deleteStatement.getTableSource() != null
					? deleteStatement.getTableSource()
					: deleteStatement.getFrom();
			List<String> affectAliasList = DruidAnalysisUtils.buildTableSourceAliases(getAliasToTableMap(),
					affectTableSource);
			SQLTableSource from = deleteStatement.getFrom() != null ? deleteStatement.getFrom()
					: deleteStatement.getTableSource();
			SQLExpr where = deleteStatement.getWhere();
			SQLSelectQueryBlock selectQueryBlock = new SQLSelectQueryBlock();
			for (String alias : affectAliasList) {
				String table = getAliasToTableMap().get(alias);
				if (metadataReader.isAuditCondition(table)) {
					metadataReader.getPrimaryKeys(table).forEach((pk) -> {
						selectQueryBlock.getSelectList()
								.add(new SQLSelectItem(SQLUtils.toSQLExpr(String.format("%s.%s", alias, pk))));
					});
					metadataReader.getAuditColumns(table).forEach(columnName -> {
						selectQueryBlock.getSelectList()
								.add(new SQLSelectItem(SQLUtils.toSQLExpr(String.format("%s.%s", alias, columnName))));
					});
				}
			}
			if (CollectionUtils.isNotEmpty(selectQueryBlock.getSelectList())) {
				selectQueryBlock.setFrom(from);
				selectQueryBlock.setWhere(where);
				String querySql = SQLParseUtils.trimSQLWhitespaces(SQLUtils.toSQLString(selectQueryBlock));
				getCurrentDataForTables(querySql);
			}
		}
	}

	@Override
	public void afterHandle() {
	}

	private void getCurrentDataForTables(String querySql) throws SQLException {
		try (PreparedStatement statement = getConnection().prepareStatement(querySql)) {
			try (ResultSet resultSet = statement.executeQuery()) {
				ResultSetMetaData meta = resultSet.getMetaData();
				Map<String, Collection<String>> primaryKeyMap = new CaseInsensitiveMap<>();
				Map<String, Map<String, Object>> primaryKeyVal = new CaseInsensitiveMap<>();
				Map<String, Map<String, Object>> columnVal = new CaseInsensitiveMap<>();
				int columnCount = meta.getColumnCount();
				for (int i = 1; i < columnCount + 1; i++) {
					String tableName = meta.getTableName(i);
					if (!primaryKeyMap.containsKey(tableName)) {
						Collection<String> pk = metadataReader.getPrimaryKeys(tableName);
						primaryKeyMap.put(tableName, pk);
					}
				}

				while (resultSet.next()) {
					for (int i = 1; i < columnCount + 1; i++) {
						String tableName = meta.getTableName(i);
						String columnName = meta.getColumnName(i);
						Object val = resultSet.getObject(i);

						if (!columnVal.containsKey(tableName)) {
							columnVal.put(tableName, new CaseInsensitiveMap<>());
						}
						columnVal.get(tableName).put(columnName, val);
						if (primaryKeyMap.containsKey(tableName) && primaryKeyMap.get(tableName).contains(columnName)) {
							if (!primaryKeyVal.containsKey(tableName)) {
								primaryKeyVal.put(tableName, new CaseInsensitiveMap<>());
							}
							primaryKeyVal.get(tableName).put(columnName, val);
						}
					}
					primaryKeyVal.forEach((table, vals) -> {
						eventContext.appendEvent(table,
								StringUtils.collectionToCommaDelimitedString(primaryKeyMap.get(table)), null,
								vals.size() > 1 ? JSONUtil.toJsonStr(vals)
										: vals.entrySet().iterator().next().getValue(),
								OperationUtils.DELETE, JSONUtil.toJsonStr(columnVal.get(table)), null);
					});
				}
			}
		}
	}

}
