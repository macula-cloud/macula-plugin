package org.macula.plugin.datalog.handler;

import org.macula.plugin.datalog.event.ChangedContext;

public interface MappedAuditHandler {

	void beforeHandle();

	void afterHandle();

	void setEventContext(ChangedContext changedContext);
}
