package org.macula.plugin.datalog.event;

import java.util.List;
import java.util.function.Consumer;

public interface ChangedContextProvider {

	ChangedContext getEventContext();

	List<Consumer<ChangedEvent>> getConsumers();

}
