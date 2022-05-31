package org.macula.plugins.execlog.interceptor;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.macula.engine.assistant.support.ApplicationId;
import org.macula.plugins.execlog.annotation.Execlog;
import org.macula.plugins.execlog.event.ExecutionContext;
import org.macula.plugins.execlog.event.ExecutionContextProvider;
import org.macula.plugins.execlog.event.ExecutionEvent;

@Aspect
@AllArgsConstructor
public class ExecutionLogAspect {

	private ExecutionContextProvider executionContextProvider;

	@Around("@annotation(execlog)")
	public Object around(ProceedingJoinPoint joinPoint, Execlog execlog) throws Throwable {
		ExecutionContext executionContext = executionContextProvider.getExecutionContext();
		executionContext.setTransactionId(Thread.currentThread().getName() + "@" + ApplicationId.current().getApplicationKey());
		executionContext.setRootObject(joinPoint, execlog);
		ExecutionEvent event = executionContext.start();
		Object result = null;
		Exception exception = null;
		try {
			result = joinPoint.proceed();
			return result;
		} catch (Exception ex) {
			exception = ex;
			throw ex;
		} finally {
			event = executionContext.end(event, result, exception);
			executionContext.append(event);
			executionContext.triggerEvents(executionContextProvider.getConsumers());
		}
	}

}
