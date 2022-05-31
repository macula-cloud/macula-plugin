package org.macula.plugin.datalog.util;

import java.util.Arrays;
import java.util.List;

public class OperationUtils {
	public static final String INSERT = "insert";

	public static final String UPDATE = "update";

	public static final String DELETE = "delete";

	public static final List<String> OPERATIONS = Arrays.asList(new String[] {
			INSERT,
			UPDATE,
			DELETE });

	public static boolean isOperation(String operation) {
		return OPERATIONS.contains(operation.toLowerCase());
	}

}