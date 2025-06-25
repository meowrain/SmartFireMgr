package com.xszx.util.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariPoolUtil {
    private static final Logger log = LoggerFactory.getLogger(HikariPoolUtil.class);
    private static final HikariDataSource hikariDataSource;

    static {
        try {
            Properties prop = new Properties();
            InputStream in = HikariPoolUtil.class.getClassLoader().getResourceAsStream("db.properties");
            prop.load(in);
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(prop.getProperty("db.url"));
            hikariConfig.setUsername(prop.getProperty("db.username"));
            hikariConfig.setPassword(prop.getProperty("db.password"));
            hikariConfig.setDriverClassName(prop.getProperty("db.driver"));
            String poolSize = prop.getProperty("db.pool.size");
            hikariConfig.setMaximumPoolSize(Integer.parseInt(poolSize));
            hikariDataSource = new HikariDataSource(hikariConfig);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("HikariCP initialization failed", e);
        }
    }
    public static Connection getConnection() throws SQLException {
        log.debug("数据库池获取连接");
        return hikariDataSource.getConnection();
    }

    public static void ClosePool() {
        log.debug("数据库池关闭");
        hikariDataSource.close();
    }
}
