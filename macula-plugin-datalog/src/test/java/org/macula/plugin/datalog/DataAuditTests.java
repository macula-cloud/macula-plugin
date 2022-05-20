package org.macula.plugin.datalog;

import org.junit.jupiter.api.Test;
import org.macula.plugin.datalog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = DataAuditApplication.class)
public class DataAuditTests {

	@Autowired
	private UserService userService;

	@Test
	public void testDataAudit() {
		userService.dataAudit();
		userService.updateEmail("wilson@gmail.com");
		userService.deleteByEmail("wilson@gmail.com");
	}

}
