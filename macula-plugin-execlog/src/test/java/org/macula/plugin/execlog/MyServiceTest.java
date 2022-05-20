package org.macula.plugin.execlog;

import org.macula.plugin.execlog.annotation.ServiceInvokeProxy;

import org.springframework.stereotype.Component;

@Component
public class MyServiceTest implements ServiceTest {

	@ServiceInvokeProxy
	public String getName(String name) {
		return "Hello:" + getInternalName(name);
	}

	public String getInternalName(String name) {
		return "InternalName:-> " + name;
	}

}
