package org.macula.plugin.datalog.event;

public interface EventContextAware {

	EventContext getEventContext();

	void processEvent(EventContext context);
}
