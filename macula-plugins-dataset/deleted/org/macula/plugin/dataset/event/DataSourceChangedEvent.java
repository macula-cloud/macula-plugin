package org.macula.plugin.dataset.event;

import org.macula.plugin.core.utils.StringUtils;
import org.macula.plugin.dataset.domain.DataSource;

import org.springframework.context.ApplicationEvent;

/**
 * <p> <b>DataSourceChangedEvent</b> 是数据源修改事件. </p>
 */
public class DataSourceChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	private final Long id;

	/**
	 * @param dataSource
	 */
	public DataSourceChangedEvent(DataSource dataSource) {
		super(dataSource != null ? dataSource.getCode() : StringUtils.EMPTY);
		this.id = dataSource == null ? null : dataSource.getId();
	}

	@Override
	public String getSource() {
		return (String) super.getSource();
	}

	public Long getDataSourceId() {
		return this.id;
	}
}
