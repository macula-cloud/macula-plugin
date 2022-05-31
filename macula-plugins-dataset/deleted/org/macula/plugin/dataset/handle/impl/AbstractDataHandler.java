package org.macula.plugin.dataset.handle.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.macula.plugin.dataset.handle.DataHandler;

import org.springframework.data.domain.Pageable;

/**
 * <p> <b>AbstractDataHandler</b> 是处理成Java对象的处理器. </p>
 */
public abstract class AbstractDataHandler implements DataHandler {

	private final Map<String, Object> extractParams = new HashMap<String, Object>();
	private Object dataSource;
	private Pageable pageable;

	@Override
	public Map<String, Object> getOutputParameters() {
		return extractParams;
	}

	@Override
	public void setInputParameters(Map<String, Object> parameters) {
		if (parameters != null) {
			extractParams.putAll(parameters);
		}
	}

	@Override
	public void setDataSource(Object dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the dataSource
	 */
	protected Object getDataSource() {
		return dataSource;
	}

	@Override
	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	@Override
	public void initialize(Properties properties) {
		// nothing
	}

	@Override
	public Properties getProperties() {
		return null;
	}

	/**
	 * @return the pageable
	 */
	protected Pageable getPageable() {
		return pageable;
	}

	/**
	 * @return the extractParams
	 */
	protected Map<String, Object> getExtractParams() {
		return extractParams;
	}

	/**
	 * 是否需要分页.
	 */
	protected boolean isPageable() {
		return pageable != null;
	}

	@Override
	public final Object handle(Object data, Object userContext) {
		return handleInternal(data, userContext.addAdditionInfo(getExtractParams()));
	}

	protected abstract Object handleInternal(Object data, Object userContext);
}
