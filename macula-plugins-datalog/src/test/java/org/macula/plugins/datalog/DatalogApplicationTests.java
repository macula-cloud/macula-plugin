package org.macula.plugins.datalog;

import org.junit.jupiter.api.Test;
import org.macula.plugins.datalog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DatalogApplication.class)
public class DatalogApplicationTests {

	@Autowired
	private UserService userService;

	@Test
	public void testDataAudit() {
		userService.dataAudit();
		userService.updateEmail("wilson@gmail.com");
		userService.deleteByEmail("wilson@gmail.com");
	}

}
