package org.macula.plugins.execlog;

import org.macula.plugins.execlog.annotation.Execlog;

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
