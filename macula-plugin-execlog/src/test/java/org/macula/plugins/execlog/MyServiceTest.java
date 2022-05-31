package org.macula.plugin.execlog;

import org.macula.plugin.execlog.annotation.Execlog;

import org.springframework.stereotype.Component;

@Component
public class MyServiceTest implements ServiceTest {

	@Execlog
	public String getName(String name) {
		return "Hello:" + getInternalName(name);
	}

	public String getInternalName(String name) {
		return "InternalName:-> " + name;
	}

}
