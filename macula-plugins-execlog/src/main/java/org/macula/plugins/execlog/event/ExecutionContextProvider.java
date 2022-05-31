package org.macula.plugins.execlog.event;

import java.util.List;
import java.util.function.Consumer;

/**
 * <p>Execution Context Provider</p>
 */
public interface ExecutionContextProvider {

	ExecutionContext getExecutionContext();

	List<Consumer<ExecutionEvent>> getConsumers();

}
