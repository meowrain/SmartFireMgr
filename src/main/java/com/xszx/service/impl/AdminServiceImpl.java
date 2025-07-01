package com.xszx.service.impl;

import com.xszx.common.exceptions.ServiceException;
import com.xszx.dao.entity.AdminDAO;
import com.xszx.dto.req.admin.LoginRequestDTO;
import com.xszx.dto.req.admin.RegisterRequestDTO;
import com.xszx.dto.resp.admin.LoginResponseDTO;
import com.xszx.dto.resp.admin.RegisterResponseDTO;
import com.xszx.service.AdminService;
import com.xszx.util.JwtUtil;
import com.xszx.util.db.JDBCTemplate;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xszx.common.errorcode.BaseErrorCode.*;


public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {

//            Connection connection = HikariPoolUtil.getConnection();
        String sql = "select * from t_admin where name = ?";
        JDBCTemplate jdbcTemplate = new JDBCTemplate();
        AdminDAO admin = jdbcTemplate.queryForObject(sql, AdminDAO.class, loginRequestDTO.getUsername());

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
        if (admin != null) {
            String hashedPasswordFromDB = admin.getPassword(); // 从数据库获取存储的哈希密码
            if (BCrypt.checkpw(loginRequestDTO.getPassword(), hashedPasswordFromDB)) {
                LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
                String jwtToken = JwtUtil.generateAccessToken(admin);
                loginResponseDTO.setUsername(admin.getName());
                loginResponseDTO.setToken(jwtToken);
                return loginResponseDTO;
            }else {
                throw new ServiceException(LOGIN_ERROR_USERNAME_PWD);
            }
        } else {
            throw new ServiceException(LOGIN_ERROR_USERNAME_PWD);
        }

    }

    @Override
    public RegisterResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        //首先检查用户是否存在
        String username = registerRequestDTO.getUsername();
        String rawPassword = registerRequestDTO.getPassword();
        JDBCTemplate jdbcTemplate = new JDBCTemplate();
        String sql = "select * from t_admin where name = ?";
        AdminDAO adminDaoQuery = jdbcTemplate.queryForObject(sql, AdminDAO.class, username);
        if (adminDaoQuery != null) {
            // 用户已经存在，不允许注册
            throw new ServiceException(REGISTER_ERROR01);
        }

        String salt = BCrypt.gensalt(10); // 10 是一个常用且安全的默认值
        //        哈希（加密）密码
        String encrptPassword = BCrypt.hashpw(rawPassword, salt);

        AdminDAO adminDao = new AdminDAO();
        adminDao.setName(username);
        adminDao.setPassword(encrptPassword);
        adminDao.setState("1");

        boolean isInserted = jdbcTemplate.insert("t_admin", adminDao);
        if (!isInserted) {
            throw new ServiceException(REGISTER_ERROR02);
        }
        return null;
    }
}
