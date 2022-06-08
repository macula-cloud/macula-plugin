package org.macula.plugins.datalog.entity;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
	private Long id;
	private String username;
	private String mobile;
	private String email;
	private BigDecimal wallet;
	private BigDecimal amount;

}
