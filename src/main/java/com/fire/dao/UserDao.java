package com.fire.dao;

import com.fire.pojo.User;
import com.fire.util.DBUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;

public class UserDao {

    public User checkLogin(String username, String password) {
        try (SqlSession sqlSession = DBUtil.getSqlSession()) {
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            params.put("password", password);
            // The string "com.fire.mapper.UserMapper.getUserByUsernameAndPassword" must
            // match the namespace and id in the mapper XML
            return sqlSession.selectOne("com.fire.mapper.UserMapper.getUserByUsernameAndPassword", params);
        }
    }
}