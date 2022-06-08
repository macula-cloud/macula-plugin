package org.macula.plugin.dataset.event;

import org.springframework.context.ApplicationEvent;

/**
 * <p> <b>DataParamChangedEvent</b> 是数据集变化事件. </p>
 */
public class DataSetChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * @param source
	 */
	public DataSetChangedEvent(String source) {
		super(source);
	}

	@Override
	public String getSource() {
		return (String) super.getSource();
	}

}
