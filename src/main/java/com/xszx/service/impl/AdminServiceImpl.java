package com.xszx.service.impl;

import com.xszx.dao.entity.AdminDao;
import com.xszx.dto.resp.LoginResponseDTO;
import com.xszx.service.AdminService;
import com.xszx.util.db.HikariPoolUtil;
import com.xszx.util.db.JDBCTemplate;

import java.sql.Connection;
import java.util.Objects;

public class AdminServiceImpl implements AdminService {
    @Override
    public LoginResponseDTO login(String name, String password) {
        try {
            Connection connection = HikariPoolUtil.getConnection();
            String sql = "select * from t_admin where name = ? and password = ?";

            JDBCTemplate jdbcTemplate = new JDBCTemplate();
            AdminDao admin = jdbcTemplate.queryForObject(sql, AdminDao.class, name, password);

//            PreparedStatement ps = connection.prepareStatement(sql);
//            ps.setString(1, name);
//            ps.setString(2, password);
//            ResultSet resultSet = ps.executeQuery();
//            AdminDao admin = new AdminDao();
//            while (resultSet.next()) {
//                admin.setId(resultSet.getLong("id"));
//                admin.setName(resultSet.getString("name"));
//                admin.setPassword(resultSet.getString("password"));
//                admin.setState(resultSet.getString("state"));
//            }
            if (Objects.equals(admin.getName(), name) && Objects.equals(admin.getPassword(), password)) {
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
                loginResponseDTO.setUsername(admin.getName());
                return loginResponseDTO;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
