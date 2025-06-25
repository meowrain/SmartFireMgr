package com.xszx;

import com.xszx.util.db.HikariPoolUtil;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DbTest {
    @Test
    public void testConnection() throws SQLException {
//        Connection connection = JDBCUtil.getConnection();
//        System.out.println(connection);
        Connection connection = HikariPoolUtil.getConnection();
        System.out.println(connection);
        HikariPoolUtil.ClosePool();
    }
}
