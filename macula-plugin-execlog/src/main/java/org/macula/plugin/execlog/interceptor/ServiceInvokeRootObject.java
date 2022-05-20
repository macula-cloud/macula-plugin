package org.macula.plugin.execlog.interceptor;

import java.lang.reflect.Method;

import lombok.Getter;

@Getter
public class ServiceInvokeRootObject {

	private final Method method;

	private final Object[] args;

	private final Object source;

	private Class<?> sourceClass;

	private final Class<?> targetClass;

	private final Method targetMethod;

	private Object result;
	private Exception e;

	public ServiceInvokeRootObject(Method method, Object[] args, Object source, Class<?> sourceClass, Class<?> targetClass, Method targetMethod) {
		this.method = method;
		this.args = args;
		this.source = source;
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.targetMethod = targetMethod;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * @param ex the ex to set
	 */
	public void setE(Exception ex) {
		this.e = ex;
	}

}
