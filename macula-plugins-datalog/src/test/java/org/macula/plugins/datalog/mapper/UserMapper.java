package org.macula.plugins.datalog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.macula.plugins.datalog.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

	@Update("update user set email = #{value}")
	void updateEmailValue(String value);

	@Delete("delete user where email = #{value}")
	void deleteByEmail(String value);
}
