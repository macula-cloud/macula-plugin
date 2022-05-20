package org.macula.plugin.datalog.meta;

public interface OperationProvider {

	default String getOperationUserId() {
		return "";
	}
}
