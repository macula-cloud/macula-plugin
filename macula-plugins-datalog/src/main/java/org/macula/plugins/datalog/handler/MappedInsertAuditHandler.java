package org.macula.plugins.datalog.handler;

import java.sql.Connection;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.macula.plugins.datalog.meta.MetadataReader;

public class MappedInsertAuditHandler extends AbstractMappedAuditHandler {

	//	private String table;
	//
	//	private List<String> columnList = new ArrayList<>();

	public MappedInsertAuditHandler(Connection connection, String insertSQL, MetadataReader metadataReader) {
		super(connection, insertSQL, metadataReader);
	}

	@Override
	protected SQLStatement parseSQLStatement(SQLStatementParser statementParser) {
		return statementParser.parseInsert();
	}

	@Override
	public void beforeHandle() {
		// TODO don't know how to get auto increment id after insert

		//		if (getSqlStatement() instanceof SQLInsertStatement) {
		//			SQLInsertStatement sqlInsertStatement = (SQLInsertStatement) getSqlStatement();
		//			if (sqlInsertStatement.getColumns().size() > 0) {
		//				SQLExpr sqlExpr = sqlInsertStatement.getColumns().get(0);
		//				String[] aliasAndColumn = SQLParseUtils.separateAliasAndColumn(SQLUtils.toSQLString(sqlExpr));
		//				if (aliasAndColumn[0] != null) {
		//					table = getAliasToTableMap().get(aliasAndColumn[0]);
		//				} else if (getTables().size() == 1) {
		//					table = getTables().iterator().next();
		//				} else {
		//					table = metadataReader.getColumnTable(getTables(), aliasAndColumn[1]);
		//				}
		//				for (int i = 0; i < sqlInsertStatement.getColumns().size(); i++) {
		//					SQLExpr columnExpr = sqlInsertStatement.getColumns().get(i);
		//					columnList.add(SQLParseUtils.separateAliasAndColumn(SQLUtils.toSQLString(columnExpr))[1]);
		//				}
		//			}
		//		}
	}

	@Override
	public void afterHandle() {
		//		try (Statement statement = getConnection().createStatement()) {
		//			String limitSQL = "SELECT rowno - 1, rowcon FROM (SELECT @rowno := @rowno + 1 AS rowno, t2.rowcon AS rowcon, ID FROM "
		//					+ table
		//					+ " r, (SELECT @rowno := 0) t, (SELECT ROW_COUNT() AS rowcon) t2 order by r.id asc) b WHERE b.ID = (SELECT LAST_INSERT_ID())";
		//			try (ResultSet limitResultSet = statement.executeQuery(limitSQL)) {
		//				if (limitResultSet.next()) {
		//					Integer limit_1 = limitResultSet.getInt(1);
		//					Integer limit_2 = limitResultSet.getInt(2);
		//					StringBuilder sb = new StringBuilder();
		//					sb.append(StringUtils.collectionToDelimitedString(metadataReader.getPrimaryKeys(table), ", "));
		//					for (String column : columnList) {
		//						sb.append(", ");
		//						sb.append(column);
		//					}
		//					try (ResultSet resultSet = statement.executeQuery(String.format(
		//							"select %s from %s where id>=(select id from %s order by id asc limit %s,1) limit %s",
		//							sb.toString(), table, table, limit_1, limit_2))) {
		//						int columnCount = resultSet.getMetaData().getColumnCount();
		//						while (resultSet.next()) {
		//							Object primaryKey = null;
		//							for (int i = 1; i < columnCount + 1; i++) {
		//								if (i == 1) {
		//									primaryKey = resultSet.getObject(i);
		//								} else {
		//									changedContext.appendEvent(table, columnList.get(i - 2), null, primaryKey,
		//											OperationUtils.INSERT, null, resultSet.getObject(i));
		//								}
		//							}
		//						}
		//					}
		//				}
		//			}
		//		} catch (SQLException e) {
		//
		//		}
	}
}
