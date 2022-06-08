package org.macula.plugins.execlog;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ExecutionLogApplication.class)
public class ExecutionLogApplicationTest {

	@Autowired
	private ServiceTest myservice;

	@Test
	public void testGetName() {
		String result = myservice.getName("Wilson");
		assertThat(result).contains("Hello", "InternalName", "Wilson");
	}

}
