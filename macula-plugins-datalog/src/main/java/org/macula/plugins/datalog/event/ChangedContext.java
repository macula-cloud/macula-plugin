package org.macula.plugins.datalog.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

public class ChangedContext {
	@Getter
	@Setter
	private String traceId;
	@Getter
	@Setter
	private String spanId;
	@Getter
	@Setter
	private String userId;
	@Getter
	@Setter
	private String applicationId;
	@Getter
	private final long time;
	private List<ChangedEvent> changedEvents = new ArrayList<>();

	public ChangedContext() {
		time = System.currentTimeMillis();
	}

	/**
	 * @param table
	 * @param column
	 * @param parentId
	 * @param entityId
	 * @param operation
	 * @param oldValue
	 * @param newValue
	 */
	@SneakyThrows
	public void appendEvent(String table, String column, Object parentId, Object entityId, String operation, Object oldValue, Object newValue) {
		changedEvents.add(new ChangedEvent(traceId, spanId, userId, applicationId, table, column, parentId, entityId, operation, oldValue, newValue));
	}

	/**
	 * @param consumers
	 */
	public void triggerEvents(List<Consumer<ChangedEvent>> consumers) {
		changedEvents.forEach(event -> consumers.forEach(c -> c.accept(event)));
	}

}
