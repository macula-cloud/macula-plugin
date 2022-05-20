package org.macula.plugin.datalog.util;

import java.sql.Connection;

import org.macula.plugin.datalog.handler.MappedAuditHandler;
import org.macula.plugin.datalog.handler.MappedDeleteAuditHandler;
import org.macula.plugin.datalog.handler.MappedInsertAuditHandler;
import org.macula.plugin.datalog.handler.MappedUpdateAuditHandler;
import org.macula.plugin.datalog.meta.MetadataReader;

public class MappedAuditHandlerFactory {

	public static MappedAuditHandler createEntityAuditHandler(Connection connection, String sqlCommandType, String sql,
			MetadataReader metadataReader) {
		if (OperationUtils.INSERT.equalsIgnoreCase(sqlCommandType)) {
			return new MappedInsertAuditHandler(connection, sql, metadataReader);
		} else if (OperationUtils.UPDATE.equalsIgnoreCase(sqlCommandType)) {
			return new MappedUpdateAuditHandler(connection, sql, metadataReader);
		} else if (OperationUtils.DELETE.equalsIgnoreCase(sqlCommandType)) {
			return new MappedDeleteAuditHandler(connection, sql, metadataReader);
		}
		return null;
	}

}
