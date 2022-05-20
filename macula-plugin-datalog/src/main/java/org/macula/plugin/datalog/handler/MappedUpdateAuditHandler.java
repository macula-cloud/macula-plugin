package org.macula.plugin.datalog.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.macula.plugin.datalog.meta.MetadataReader;
import org.macula.plugin.datalog.util.OperationUtils;
import org.macula.plugin.datalog.util.SQLParseUtils;

import org.springframework.util.StringUtils;

public class MappedUpdateAuditHandler extends AbstractMappedAuditHandler {

	private Map<String, List<String>> updateColumnListMap = new CaseInsensitiveMap<>();

	private Map<String, Map<String, Object[]>> rowsBeforeUpdateListMap = null;

	public MappedUpdateAuditHandler(Connection connection, String updateSQL, MetadataReader metadataReader) {
		super(connection, updateSQL, metadataReader);
	}

	@Override
	protected SQLStatement parseSQLStatement(SQLStatementParser statementParser) {
		return statementParser.parseUpdateStatement();
	}

	@Override
	public void beforeHandle() {
		if (getSqlStatement() instanceof SQLUpdateStatement) {
			SQLUpdateStatement updateStatement = (SQLUpdateStatement) getSqlStatement();
			SQLTableSource tableSource = updateStatement.getTableSource();
			List<SQLUpdateSetItem> updateSetItems = updateStatement.getItems();
			SQLExpr where = updateStatement.getWhere();

			for (SQLUpdateSetItem sqlUpdateSetItem : updateSetItems) {
				String selectItem = SQLUtils.toSQLString(sqlUpdateSetItem.getColumn());
				String aliasAndColumn[] = SQLParseUtils.separateAliasAndColumn(selectItem);
				String alias = aliasAndColumn[0];
				String column = aliasAndColumn[1];
				String table = null;
				if (StringUtils.hasText(alias)) {
					table = getAliasToTableMap().get(alias);
				} else if (getTables().size() == 1) {
					table = getTables().iterator().next();
				} else {
					table = metadataReader.getColumnTable(getTables(), column);
				}

				if (metadataReader.isAuditCondition(table, column)) {
					if (!updateColumnListMap.containsKey(table)) {
						updateColumnListMap.put(table, new ArrayList<>());
					}
					updateColumnListMap.get(table).add(column);
				}
			}

			SQLSelectQueryBlock selectQueryBlock = new SQLSelectQueryBlock();
			selectQueryBlock.setFrom(tableSource);
			selectQueryBlock.setWhere(where);

			for (Map.Entry<String, List<String>> updateInfoListEntry : updateColumnListMap.entrySet()) {
				String table = updateInfoListEntry.getKey();
				String alias = getTableToAliasMap().get(table);
				metadataReader.getPrimaryKeys(table).forEach((pk) -> {
					selectQueryBlock.getSelectList()
							.add(new SQLSelectItem(SQLUtils.toSQLExpr(String.format("%s.%s", alias, pk))));
				});
				for (String column : updateInfoListEntry.getValue()) {
					selectQueryBlock.getSelectList()
							.add(new SQLSelectItem(SQLUtils.toSQLExpr(String.format("%s.%s", alias, column))));
				}
			}

			if (CollectionUtils.isNotEmpty(selectQueryBlock.getSelectList())) {
				rowsBeforeUpdateListMap = getTablesData(
						SQLParseUtils.trimSQLWhitespaces(SQLUtils.toSQLString(selectQueryBlock)), updateColumnListMap);
			}
		}
	}

	@Override
	public void afterHandle() {
		if (rowsBeforeUpdateListMap != null) {
			Map<String, Map<String, Object[]>> rowsAfterUpdateListMap = getTablesDataAfterUpdate();
			for (String tableName : rowsBeforeUpdateListMap.keySet()) {
				Map<String, Object[]> rowsBeforeUpdateRowsMap = rowsBeforeUpdateListMap.get(tableName);
				Map<String, Object[]> rowsAfterUpdateRowsMap = rowsAfterUpdateListMap.get(tableName);
				if (rowsBeforeUpdateRowsMap != null && rowsAfterUpdateRowsMap != null) {
					for (String pKey : rowsBeforeUpdateRowsMap.keySet()) {
						Object[] rowBeforeUpdate = rowsBeforeUpdateRowsMap.get(pKey);
						Object[] rowAfterUpdate = rowsAfterUpdateRowsMap.get(pKey);
						for (int col = 0; col < rowBeforeUpdate.length; col++) {
							if (rowBeforeUpdate[col] != null	&& !rowBeforeUpdate[col].equals(rowAfterUpdate[col])
								|| rowBeforeUpdate[col] == null && rowAfterUpdate[col] != null) {
								eventContext.appendEvent(tableName, updateColumnListMap.get(tableName).get(col), null,
										pKey, OperationUtils.UPDATE, rowBeforeUpdate[col], rowAfterUpdate[col]);
							}
						}
					}
				}
			}
		}
	}

	@SneakyThrows
	private Map<String, Map<String, Object[]>> getTablesDataAfterUpdate() {
		Map<String, Map<String, Object[]>> resultListMap = new CaseInsensitiveMap<>();
		for (Map.Entry<String, Map<String, Object[]>> tableDataEntry : rowsBeforeUpdateListMap.entrySet()) {
			String tableName = tableDataEntry.getKey();
			Map<String, Object[]> tableValue = tableDataEntry.getValue();
			SQLSelectQueryBlock selectQueryBlock = new SQLSelectQueryBlock();
			Collection<String> pkeys = metadataReader.getPrimaryKeys(tableName);
			pkeys.forEach((pk) -> {
				selectQueryBlock.getSelectList().add(new SQLSelectItem(SQLUtils.toSQLExpr(pk)));
			});
			for (String column : updateColumnListMap.get(tableName)) {
				selectQueryBlock.getSelectList().add(new SQLSelectItem(SQLUtils.toSQLExpr(column)));
			}

			Object[] pkColumns = pkeys.toArray();
			// Single primary column, use ID in (1,2,3)
			if (pkColumns.length < 1) {
				SQLInListExpr sqlInListExpr = new SQLInListExpr();
				List<SQLExpr> sqlExprList = new ArrayList<>();
				for (String pVals : tableValue.keySet()) {
					sqlExprList.add(SQLUtils.toSQLExpr(pVals));
				}
				sqlInListExpr.setExpr(new SQLIdentifierExpr(pkColumns[0].toString()));
				sqlInListExpr.setTargetList(sqlExprList);
				selectQueryBlock.setWhere(sqlInListExpr);
			} else {
				// Multiple primary column, use (ID1 = a1 and ID2 = b1) or (ID1 = a2 and ID2 = b2)
				SQLExpr multiRecord = null;
				for (String pVals : tableValue.keySet()) {
					String[] keys = StringUtils.commaDelimitedListToStringArray(pVals);
					SQLExpr item = null;
					for (int i = 0; i < keys.length; i++) {
						SQLExpr columnItem = new SQLBinaryOpExpr(new SQLIdentifierExpr(pkColumns[i].toString()),
								SQLBinaryOperator.Equality, new SQLVariantRefExpr(keys[i]));
						item = SQLBinaryOpExpr.and(item, columnItem);
					}
					multiRecord = SQLBinaryOpExpr.or(multiRecord, item);
				}
				selectQueryBlock.addWhere(multiRecord);
			}

			selectQueryBlock.setFrom(new SQLExprTableSource(new SQLIdentifierExpr(tableName)));
			Map<String, List<String>> tableColumnMap = new CaseInsensitiveMap<>();
			tableColumnMap.put(tableName, updateColumnListMap.get(tableName));
			Map<String, Map<String, Object[]>> map = getTablesData(
					SQLParseUtils.trimSQLWhitespaces(SQLUtils.toSQLString(selectQueryBlock)), tableColumnMap);
			resultListMap.putAll(map);
		}
		return resultListMap;
	}

	@SneakyThrows
	/**
	 * 
	 * @param querySQL
	 * @param tableColumnsMap <TableName, List<ColumnName>
	 * @return Map <TableName, Map<PrimaryKey-Value-Object, OtherColumn-Value-Object[]>
	 */
	private Map<String, Map<String, Object[]>> getTablesData(String querySQL,
			Map<String, List<String>> tableColumnsMap) {
		Map<String, Map<String, Object[]>> resultListMap = new CaseInsensitiveMap<>();
		try (PreparedStatement statement = getConnection().prepareStatement(querySQL)) {
			try (ResultSet resultSet = statement.executeQuery()) {
				int columnCount = resultSet.getMetaData().getColumnCount();
				while (resultSet.next()) {
					Map<String, List<Object>> currRowTablePKeyMap = new CaseInsensitiveMap<>();
					Map<String, List<Object>> currRowTableColumnMap = new CaseInsensitiveMap<>();
					// Get primary key value
					for (int i = 1; i < columnCount + 1; i++) {
						String table = resultSet.getMetaData().getTableName(i);
						String column = resultSet.getMetaData().getColumnName(i);

						if (!currRowTablePKeyMap.containsKey(table)) {
							currRowTablePKeyMap.put(table, new ArrayList<>());
						}

						if (!currRowTableColumnMap.containsKey(table)) {
							currRowTableColumnMap.put(table, new ArrayList<>());
						}

						if (metadataReader.getPrimaryKeys(table).contains(column)) {
							currRowTablePKeyMap.get(table).add(resultSet.getObject(i));
						} else {
							currRowTableColumnMap.get(table).add(resultSet.getObject(i));
						}
					}

					for (Map.Entry<String, List<Object>> entry : currRowTablePKeyMap.entrySet()) {
						String table = entry.getKey();
						List<Object> keyVals = entry.getValue();
						if (!resultListMap.containsKey(table)) {
							resultListMap.put(table, new CaseInsensitiveMap<>());
						}
						resultListMap.get(table).put(StringUtils.collectionToCommaDelimitedString(keyVals),
								currRowTableColumnMap.get(table).toArray(new Object[] {}));
					}
				}
			}
		}
		return resultListMap;
	}

}
