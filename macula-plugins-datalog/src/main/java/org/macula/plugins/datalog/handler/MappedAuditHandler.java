package org.macula.plugins.datalog.handler;

import org.macula.plugins.datalog.event.ChangedContext;

public interface MappedAuditHandler {

	void beforeHandle();

	void afterHandle();

	void setEventContext(ChangedContext changedContext);
}
