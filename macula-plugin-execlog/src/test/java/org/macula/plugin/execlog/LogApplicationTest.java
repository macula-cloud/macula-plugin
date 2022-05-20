package org.macula.plugin.execlog;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan
@EnableJpaRepositories
@SpringBootTest(classes = LogApplicationTest.class)
public class LogApplicationTest {

	@Autowired
	private ServiceTest myservice;

	@Test
	public void testGetName() {
		String result = myservice.getName("Wilson");
		System.out.println(result);
	}

}
