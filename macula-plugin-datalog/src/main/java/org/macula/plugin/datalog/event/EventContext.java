package org.macula.plugin.datalog.event;

import java.util.ArrayList;
import java.util.List;

public class EventContext {

	private String traceId;
	private String userId;
	private String applicationId;
	private long time;
	private List<ChangedEvent> changedEvents = new ArrayList<>();

	public EventContext() {
		time = System.currentTimeMillis();
	}

	/**
	 * @return the traceId
	 */
	public String getTraceId() {
		return this.traceId;
	}

	/**
	 * @return the traceTime
	 */
	public long getTime() {
		return this.time;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId() {
		return this.applicationId;
	}

	/**
	 * @return the changedEvents
	 */
	public List<ChangedEvent> getChangedEvents() {
		return this.changedEvents;
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
	public void appendEvent(String table, String column, Long parentId, Object entityId, String operation,
			Object oldValue, Object newValue) {
		changedEvents.add(new ChangedEvent(traceId, userId, applicationId, table, column, parentId, entityId, operation,
				oldValue, newValue));
	}

}
