package org.macula.plugin.dataset.event;

import org.springframework.context.ApplicationEvent;

/**
 * <p> <b>DataParamChangedEvent</b> 是参数变化事件. </p>
 * 
 */
public class DataParamChangedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * @param source
	 */
	public DataParamChangedEvent(String source) {
		super(source);
	}

	@Override
	public String getSource() {
		return (String) super.getSource();
	}
}
