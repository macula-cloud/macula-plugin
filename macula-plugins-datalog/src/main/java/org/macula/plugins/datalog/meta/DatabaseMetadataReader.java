package org.macula.plugins.datalog.meta;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
public class DatabaseMetadataReader implements MetadataReader, InitializingBean {

	private final Set<String> tables = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	private final Map<String, Collection<String>> primaryKeys = new CaseInsensitiveMap<String, Collection<String>>();
	private final Map<String, Collection<String>> tableColumns = new CaseInsensitiveMap<String, Collection<String>>();
	private final Map<String, Collection<String>> auditColumns = new CaseInsensitiveMap<String, Collection<String>>();
	private final DataSource datasource;

	public DatabaseMetadataReader(DataSource datasource) {
		this.datasource = datasource;
	}

	private void initialized(Connection connection) {
		log.info("[Macula] | == InitializingBean DatabaseMetadataReader, RetrieveTables & RetrieveColumns");
		retrieveTables(connection);
		tables.stream().forEach((table) -> buildOneSingleTableMetaData(connection, table));
	}

	private void buildOneSingleTableMetaData(Connection connection, String tableName) {
		primaryKeys.put(tableName, retrievePrimaryKey(connection, tableName));
		tableColumns.put(tableName, retrieveColumns(connection, tableName));
	}

	private void retrieveTables(Connection connection) {
		try (ResultSet resultSet = connection.getMetaData().getTables(connection.getCatalog(), null, null,
				new String[] {
						"TABLE" })) {
			while (resultSet.next()) {
				String tableName = resultSet.getString("TABLE_NAME");
				tables.add(tableName);
			}
		} catch (SQLException e) {
			log.error("[Macula] |- DataLog - RetrieveTables error:", e);
		}
	}

	private Set<String> retrievePrimaryKey(Connection connection, String table) {
		Set<String> primaryKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		try (ResultSet resultSet = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), null, table)) {
			if (resultSet.next()) {
				primaryKeys.add(resultSet.getString("COLUMN_NAME"));
			}
		} catch (SQLException e) {
			log.error("[Macula] |- DataLog - RetrievePrimaryKey error:", e);
		}
		return primaryKeys;
	}

	private Set<String> retrieveColumns(Connection connection, String table) {
		Set<String> columns = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		try (ResultSet resultSet = connection.getMetaData().getColumns(null, null, table, null)) {
			while (resultSet.next()) {
				String columnName = resultSet.getString("COLUMN_NAME");
				columns.add(columnName);
			}
		} catch (SQLException e) {
			log.error("[Macula] |- DataLog - RetrieveColumns error:", e);
		}
		return columns;
	}

	@SneakyThrows
	@Override
	public void afterPropertiesSet() {
		try (Connection connection = datasource.getConnection()) {
			initialized(connection);
		}
	}

	@Override
	public Set<String> getTables() {
		return tables;
	}

	@Override
	public Collection<String> getColumns(String table) {
		return tableColumns.get(table);
	}

	@Override
	public Collection<String> getPrimaryKeys(String table) {
		return primaryKeys.get(table);
	}

	@Override
	public String getColumnTable(Collection<String> tables, String column) {
		for (String table : tables) {
			if (tableColumns.get(table).contains(column)) {
				return table;
			}
		}
		return null;
	}

	@Override
	public Collection<String> getAuditColumns(String table) {
		if (!auditColumns.containsKey(table)) {
			Set<String> columns = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
			columns.addAll(CollectionUtils.subtract(getColumns(table), getPrimaryKeys(table)));
			auditColumns.put(table, columns);
		}
		return auditColumns.get(table);
	}

	@Override
	public boolean isAuditCondition(String table) {
		return StringUtils.hasText(table) && CollectionUtils.isNotEmpty(getAuditColumns(table));
	}

	@Override
	public boolean isAuditCondition(String table, String column) {
		return StringUtils.hasText(table)
				&& CollectionUtils.isNotEmpty(getAuditColumns(table))
				&& getAuditColumns(table).contains(column);
	}
}
