package org.macula.plugin.datalog.service;

import javax.annotation.Resource;

import org.macula.plugin.datalog.entity.User;
import org.macula.plugin.datalog.mapper.UserMapper;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
	@Resource
	private UserMapper userMapper;
	@Resource
	private ApplicationEventPublisher applicationEventPublisher;

	@Transactional(rollbackFor = Exception.class)
	public void dataAudit() {
		User user = userMapper.selectById(1L);
		user.setEmail("wilson@aciplaw.com");
		userMapper.updateById(user);
	}

	@Transactional(rollbackFor = Exception.class)
	public void updateEmail(String value) {
		userMapper.updateEmailValue(value);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deleteByEmail(String value) {
		userMapper.deleteByEmail(value);
	}
}
