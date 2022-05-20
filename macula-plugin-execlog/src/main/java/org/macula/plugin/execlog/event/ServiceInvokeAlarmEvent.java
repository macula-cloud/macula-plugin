package org.macula.plugin.execlog.event;

import org.macula.plugin.execlog.domain.ServiceInvokeLog;

import org.springframework.context.ApplicationEvent;

public class ServiceInvokeAlarmEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public ServiceInvokeAlarmEvent(ServiceInvokeLog source) {
		super(source);
	}

}
