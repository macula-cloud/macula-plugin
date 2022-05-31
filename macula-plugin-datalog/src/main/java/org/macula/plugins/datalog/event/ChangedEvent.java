package org.macula.plugin.datalog.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class ChangedEvent {

	private final String traceId;
	private final String spanId;
	private final String userId;
	private final String applicationId;
	private final String table;
	private final String column;
	private final Object parentId;
	private final Object entityId;
	private final String operation;
	private final Object oldValue;
	private final Object newValue;
	private final long changedTime = System.currentTimeMillis();

}
