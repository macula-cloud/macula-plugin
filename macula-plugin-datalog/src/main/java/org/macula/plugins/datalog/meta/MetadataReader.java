package org.macula.plugin.datalog.meta;

import java.util.Collection;
import java.util.Set;

public interface MetadataReader {

	/** Get database tables */
	Set<String> getTables();

	/** Get table all columns ( include primary key columns )  */
	Collection<String> getColumns(String table);

	/** Get table primary key columns */
	Collection<String> getPrimaryKeys(String table);

	/** Get auditable columns */
	Collection<String> getAuditColumns(String table);

	/** Get column belongs table */
	String getColumnTable(Collection<String> tables, String column);

	/**  Should record table data change log */
	boolean isAuditCondition(String table);

	/**  Should record table column data change log */
	boolean isAuditCondition(String table, String column);
}
