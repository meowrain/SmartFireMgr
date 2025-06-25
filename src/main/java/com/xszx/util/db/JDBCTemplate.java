package com.xszx.util.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JDBCTemplate {
    private static final Logger log = LoggerFactory.getLogger(JDBCTemplate.class);

    // 缓存类的setter方法，提高性能
    private static final Map<String, Map<String, Method>> setterMethodCache = new ConcurrentHashMap<>();

    /**
     * 执行更新操作（INSERT、UPDATE、DELETE）
     *
     * @param sql    SQL语句
     * @param params SQL参数
     * @return 受影响的行数
     */
    public int update(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return 0;
        }

        try (Connection connection = HikariPoolUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // 设置SQL参数
            setParameters(pstmt, params);

            //执行更新操作
            int result = pstmt.executeUpdate();
            log.debug("执行SQL更新成功，影响行数: {}", result);
            return result;
        } catch (SQLException e) {
            log.error("数据更新失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * ORM 查询：查询多条记录，并将结果集封装到指定类型的对象列表中
     *
     * @param sql    查询语句
     * @param clazz  要封装成的目标类的 Class 对象
     * @param params SQL 参数
     * @param <T>    泛型
     * @return 目标对象列表
     */
    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return new ArrayList<>();
        }
        if (clazz == null) {
            log.warn("目标类不能为空");
            return new ArrayList<>();
        }

        List<T> list = new ArrayList<>();
        try (Connection connection = HikariPoolUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                // 获取或缓存setter方法
                Map<String, Method> setterMethods = getSetterMethods(clazz);

                while (rs.next()) {
                    try {
                        // 通过反射创建目标对象实例,必须要有无参构造器
                        T obj = clazz.getDeclaredConstructor().newInstance();

                        for (int i = 1; i <= columnCount; i++) {
                            String columnLabel = metaData.getColumnLabel(i);
                            Object columnValue = rs.getObject(columnLabel);

                            // 跳过NULL值
                            if (columnValue == null) {
                                continue;
                            }

                            String fieldName = snakeToCamel(columnLabel);
                            String setterKey = fieldName.toLowerCase();

                            Method setterMethod = setterMethods.get(setterKey);
                            if (setterMethod != null) {
                                try {
                                    // 类型转换处理
                                    Object convertedValue = convertValue(columnValue, setterMethod.getParameterTypes()[0]);
                                    setterMethod.invoke(obj, convertedValue);
                                } catch (Exception e) {
                                    log.warn("设置属性值失败 - 字段: {}, 值: {}, 错误: {}", fieldName, columnValue, e.getMessage());
                                }
                            } else {
                                log.debug("未找到对应的setter方法: set{}", capitalize(fieldName));
                            }
                        }
                        list.add(obj);
                    } catch (Exception e) {
                        log.error("创建对象实例失败: {}", e.getMessage(), e);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("数据查询失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
        }

        log.debug("查询完成，返回 {} 条记录", list.size());
        return list;
    }

    /**
     * ORM 查询：查询单条记录，并将结果集封装到指定类型的对象中
     *
     * @param sql    查询语句
     * @param clazz  要封装成的目标类的 Class 对象
     * @param params SQL 参数
     * @param <T>    泛型
     * @return 目标对象，如果未找到则返回 null
     */
    public <T> T queryForObject(String sql, Class<T> clazz, Object... params) {
        List<T> list = queryForList(sql, clazz, params);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    /**
     * 查询单个值（如count、sum等聚合函数结果）
     *
     * @param sql    查询语句
     * @param params SQL参数
     * @return 查询结果，可能为null
     */
    public Object queryForValue(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return null;
        }

        try (Connection connection = HikariPoolUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getObject(1);
                }
            }
        } catch (SQLException e) {
            log.error("查询单值失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 查询并返回Map列表，每行数据作为一个Map
     *
     * @param sql    查询语句
     * @param params SQL参数
     * @return Map列表
     */
    public List<Map<String, Object>> queryForMapList(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection connection = HikariPoolUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnLabel = metaData.getColumnLabel(i);
                        Object columnValue = rs.getObject(columnLabel);
                        row.put(columnLabel, columnValue);
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            log.error("查询Map列表失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
        }

        return result;
    }

    /**
     * 批量执行更新操作
     *
     * @param sql         SQL语句
     * @param batchParams 批量参数，每个数组对应一次执行的参数
     * @return 每次执行影响的行数数组
     */
    public int[] batchUpdate(String sql, List<Object[]> batchParams) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return new int[0];
        }
        if (batchParams == null || batchParams.isEmpty()) {
            log.warn("批量参数不能为空");
            return new int[0];
        }

        try (Connection connection = HikariPoolUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false); // 开启事务

            for (Object[] params : batchParams) {
                setParameters(pstmt, params);
                pstmt.addBatch();
            }

            int[] result = pstmt.executeBatch();
            connection.commit(); // 提交事务

            log.debug("批量更新完成，共处理 {} 条记录", result.length);
            return result;

        } catch (SQLException e) {
            log.error("批量更新失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
            return new int[0];
        }
    }

    /**
     * 获取类的setter方法缓存
     *
     * @param clazz 目标类
     * @return setter方法映射
     */
    private Map<String, Method> getSetterMethods(Class<?> clazz) {
        String className = clazz.getName();
        return setterMethodCache.computeIfAbsent(className, k -> {
            Map<String, Method> methods = new HashMap<>();
            for (Method method : clazz.getMethods()) {
                if (method.getName().startsWith("set") &&
                        method.getParameterCount() == 1 &&
                        method.getName().length() > 3) {
                    String fieldName = method.getName().substring(3).toLowerCase();
                    methods.put(fieldName, method);
                }
            }
            return methods;
        });
    }

    /**
     * 类型转换处理
     *
     * @param value      原始值
     * @param targetType 目标类型
     * @return 转换后的值
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // 如果类型已经匹配，直接返回
        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        try {
            // 处理基本类型和包装类型
            if (targetType == String.class) {
                return value.toString();
            } else if (targetType == int.class || targetType == Integer.class) {
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                }
                return Integer.parseInt(value.toString());
            } else if (targetType == long.class || targetType == Long.class) {
                if (value instanceof Number) {
                    return ((Number) value).longValue();
                }
                return Long.parseLong(value.toString());
            } else if (targetType == double.class || targetType == Double.class) {
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return Double.parseDouble(value.toString());
            } else if (targetType == float.class || targetType == Float.class) {
                if (value instanceof Number) {
                    return ((Number) value).floatValue();
                }
                return Float.parseFloat(value.toString());
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                if (value instanceof Boolean) {
                    return value;
                }
                return Boolean.parseBoolean(value.toString());
            } else if (targetType == BigDecimal.class) {
                if (value instanceof BigDecimal) {
                    return value;
                }
                return new BigDecimal(value.toString());
            } else if (targetType == LocalDate.class) {
                if (value instanceof Date) {
                    return ((Date) value).toLocalDate();
                }
                return LocalDate.parse(value.toString());
            } else if (targetType == LocalDateTime.class) {
                if (value instanceof Timestamp) {
                    return ((Timestamp) value).toLocalDateTime();
                }
                return LocalDateTime.parse(value.toString());
            }
        } catch (Exception e) {
            log.warn("类型转换失败: {} -> {}, 值: {}", value.getClass().getSimpleName(),
                    targetType.getSimpleName(), value);
        }

        // 如果转换失败，返回原值
        return value;
    }

    /**
     * 辅助方法：为 PreparedStatement 设置参数
     *
     * @param pstmt  PreparedStatement 对象
     * @param params 参数数组
     * @throws SQLException
     */
    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                // JDBC参数索引从1开始
                if (param == null) {
                    pstmt.setNull(i + 1, Types.NULL);
                } else {
                    pstmt.setObject(i + 1, param);
                }
            }
        }
    }

    /**
     * 辅助方法：将下划线命名(snake_case)转换为驼峰命名(camelCase)
     * 例如: user_name -> userName
     *
     * @param snakeCase 下划线命名的字符串
     * @return 驼峰命名的字符串
     */
    private String snakeToCamel(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        StringBuilder sb = new StringBuilder();
        boolean nextUpperCase = false;
        for (char c : snakeCase.toLowerCase().toCharArray()) {
            if (c == '_') {
                nextUpperCase = true;
            } else {
                if (nextUpperCase) {
                    sb.append(Character.toUpperCase(c));
                    nextUpperCase = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 辅助方法：首字母大写
     *
     * @param str 原字符串
     * @return 首字母大写的字符串
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 检查连接池状态
     *
     * @return 连接池是否可用
     */
    public boolean isConnectionPoolHealthy() {
        try (Connection connection = HikariPoolUtil.getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            log.error("检查连接池状态失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 执行查询并返回是否存在结果
     *
     * @param sql    查询SQL
     * @param params 参数
     * @return 是否存在结果
     */
    public boolean exists(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        try (Connection connection = HikariPoolUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("检查记录存在性失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
            return false;
        }
    }
}
