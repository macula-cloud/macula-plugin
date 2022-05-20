package org.macula.plugin.datalog.handler;

import org.macula.plugin.datalog.event.EventContext;

public interface MappedAuditHandler {

	void beforeHandle();

	void afterHandle();

	void setEventContext(EventContext eventContext);
}
