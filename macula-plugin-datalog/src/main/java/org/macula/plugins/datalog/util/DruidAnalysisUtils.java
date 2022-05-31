package org.macula.plugin.datalog.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

public class DruidAnalysisUtils {

	public static SQLTableSource getMajorTableSource(SQLStatement statement) {
		if (statement instanceof SQLInsertStatement) {
			return ((SQLInsertStatement) statement).getTableSource();
		}
		if (statement instanceof SQLUpdateStatement) {
			return ((SQLUpdateStatement) statement).getTableSource();
		}
		if (statement instanceof SQLDeleteStatement) {
			return ((SQLDeleteStatement) statement).getFrom() != null ? ((SQLDeleteStatement) statement).getFrom()
					: ((SQLDeleteStatement) statement).getTableSource();
		}
		return null;
	}

	public static Map<String, String> buildAliasToTableMap(SQLTableSource tableSource) {
		Map<String, String> map = new CaseInsensitiveMap<String, String>();
		if (tableSource instanceof SQLJoinTableSource) {
			map.putAll(buildAliasToTableMap(((SQLJoinTableSource) tableSource).getLeft()));
			map.putAll(buildAliasToTableMap(((SQLJoinTableSource) tableSource).getRight()));
		} else if (tableSource instanceof SQLExprTableSource) {
			SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
			String alias = exprTableSource.getAlias();
			if (alias == null) {
				if (exprTableSource.getExpr() instanceof SQLName) {
					alias = ((SQLName) exprTableSource.getExpr()).getSimpleName();
				}
			}
			map.put(SQLParseUtils.normalize(alias), ((SQLName) exprTableSource.getExpr()).getSimpleName());
		}
		return map;
	}

	public static Map<String, String> reverseKeyAndValueOfMap(Map<String, String> map) {
		Map<String, String> resultMap = new CaseInsensitiveMap<String, String>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			resultMap.put(val, key);
		}
		return resultMap;
	}

	public static List<String> buildTableSourceAliases(Map<String, String> tableToAliasMap,
			SQLTableSource tableSource) {
		List<String> tables = new ArrayList<>();
		if (tableSource instanceof SQLExprTableSource) {
			SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
			if (expr instanceof SQLPropertyExpr) {
				tables.add(SQLUtils.toSQLString(((SQLPropertyExpr) expr).getOwner()));
			} else if (expr instanceof SQLIdentifierExpr) {
				tables.add(tableToAliasMap.get(((SQLIdentifierExpr) expr).getName()));
			}
		} else if (tableSource instanceof SQLJoinTableSource) {
			tables.addAll(buildTableSourceAliases(tableToAliasMap, ((SQLJoinTableSource) tableSource).getLeft()));
			tables.addAll(buildTableSourceAliases(tableToAliasMap, ((SQLJoinTableSource) tableSource).getRight()));
		}
		return tables;
	}
}
