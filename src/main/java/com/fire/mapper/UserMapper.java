package com.fire.mapper;

import com.fire.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User getUserByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
}
