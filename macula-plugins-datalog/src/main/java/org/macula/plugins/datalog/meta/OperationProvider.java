package org.macula.plugins.datalog.meta;

public interface OperationProvider {

	default String getOperationUserId() {
		return "";
	}
}
