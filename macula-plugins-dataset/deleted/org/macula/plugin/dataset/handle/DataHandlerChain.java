package org.macula.plugin.dataset.handle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.util.Assert;

/**
 * <p>
 * <b>DataHandlerChain</b> 是处理数据的链表.
 * <li>1.处理器链表要求上当前处理器的要求输入参数类型比如可从上一个处理器的输出参数转化过来.</li>
 * <li>2.第一个处理器的输入参数必须是String类型.</li>
 * </p>
 */
public final class DataHandlerChain {

	private final DataHandler[] handlers;
	private final Map<String, Object> initialParams = new HashMap<String, Object>();
	private Map<String, Object> handleParams;

	public DataHandlerChain(DataHandler... processors) {
		this.handlers = processors;
		validate();
	}

	/**
	 * 转换数据.
	 * 
	 * @param origin 字符串类型的数据
	 * @param userContext 用户执行上下文
	 * @param dataSource 所需要使用的数据源
	 * @param pageable 分页信息
	 */
	public Object handle(String origin, Object userContext, Object dataSource, Pageable pageable) {
		Object handleResult = origin;
		handleParams = initialParams;
		for (DataHandler dataHandler : handlers) {
			dataHandler.setPageable(pageable);
			dataHandler.setDataSource(dataSource);
			dataHandler.setInputParameters(handleParams);
			handleResult = dataHandler.handle(handleResult, userContext);
			handleParams = dataHandler.getOutputParameters();
		}
		return handleResult;
	}

	public Map<String, Object> getExtractParamters() {
		return handleParams;
	}

	private void validate() {
		Assert.notEmpty(handlers, "Processors can't be empty!");
		Assert.state(handlers[0].getInputClass().equals(String.class), "First proccessor should support String data!");
		if (handlers.length > 1) {
			for (int i = 1; i < handlers.length; i++) {
				Assert.isAssignable(handlers[i].getInputClass(), handlers[i - 1].getOutputClass(),
						"The " + handlers[i].getName() + " not support follow the " + handlers[i - 1].getName());
			}
		}
	}

	/**
	 * @return the handlers
	 */
	public DataHandler[] getHandlers() {
		return handlers == null ? null : Arrays.copyOf(handlers, handlers.length);
	}

	/**
	 * 增加初始参数
	 */
	public void addInitialParameter(String key, Object value) {
		if (value instanceof Serializable) {
			initialParams.put(key, value);
		} else {
			initialParams.put(key, String.valueOf(value));

		}
	}
}
