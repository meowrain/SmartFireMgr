package com.xszx.util.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Deprecated
public class JDBCUtil {
    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = JDBCUtil.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(inputStream);
            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            driver = properties.getProperty("db.driver");
            // 加载数据库驱动
            Class.forName(driver);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("初始化数据库配置失败", e);
        }
    }
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,username,password);
    }
}
