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

            // 执行更新操作
            int result = pstmt.executeUpdate();
            log.debug("执行SQL更新成功，影响行数: {}", result);
            return result;
        } catch (SQLException e) {
            log.error("数据更新失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 插入数据并返回生成的主键
     *
     * @param sql    插入SQL语句
     * @param params SQL参数
     * @return 生成的主键值，如果没有则返回null
     */
    public Object insertAndReturnKey(String sql, Object... params) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return null;
        }

        try (Connection connection = HikariPoolUtil.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(pstmt, params);

            int result = pstmt.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Object key = generatedKeys.getObject(1);
                        log.debug("插入成功，生成主键: {}", key);
                        return key;
                    }
                }
            }
            log.debug("插入完成，影响行数: {}", result);
            return null;
        } catch (SQLException e) {
            log.error("插入数据失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 批量插入数据，优化性能
     *
     * @param sql         插入SQL语句
     * @param batchParams 批量参数
     * @return 成功插入的行数
     */
    public int batchInsert(String sql, List<Object[]> batchParams) {
        if (sql == null || sql.trim().isEmpty()) {
            log.warn("SQL语句不能为空");
            return 0;
        }
        if (batchParams == null || batchParams.isEmpty()) {
            log.warn("批量参数不能为空");
            return 0;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = HikariPoolUtil.getConnection();
            connection.setAutoCommit(false);

            pstmt = connection.prepareStatement(sql);

            int successCount = 0;
            int batchSize = 1000; // 批次大小

            for (int i = 0; i < batchParams.size(); i++) {
                Object[] params = batchParams.get(i);
                setParameters(pstmt, params);
                pstmt.addBatch();

                // 每1000条执行一次批量操作
                if ((i + 1) % batchSize == 0 || i == batchParams.size() - 1) {
                    int[] results = pstmt.executeBatch();
                    for (int result : results) {
                        if (result > 0) {
                            successCount++;
                        }
                    }
                    pstmt.clearBatch();
                }
            }

            connection.commit();
            log.debug("批量插入完成，成功插入 {} 条记录", successCount);
            return successCount;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    log.error("回滚失败", ex);
                }
            }
            log.error("批量插入失败，SQL: {}, 错误信息: {}", sql, e.getMessage(), e);
            return 0;
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("关闭资源失败", e);
            }
        }
    }

    /**
     * 分页查询
     *
     * @param sql      查询SQL语句（不包含LIMIT）
     * @param clazz    目标类
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页大小
     * @param params   SQL参数
     * @param <T>      泛型
     * @return 分页结果
     */
    public <T> PageResult<T> queryForPage(String sql, Class<T> clazz, int pageNum, int pageSize, Object... params) {
        if (pageNum < 1)
            pageNum = 1;
        if (pageSize < 1)
            pageSize = 10;

        // 构建分页SQL
        int offset = (pageNum - 1) * pageSize;
        String pageSql = sql + " LIMIT " + pageSize + " OFFSET " + offset;

        // 查询数据
        List<T> data = queryForList(pageSql, clazz, params);

        // 查询总数（构建COUNT SQL）
        String countSql = buildCountSql(sql);
        Object totalObj = queryForValue(countSql, params);
        long total = totalObj != null ? ((Number) totalObj).longValue() : 0;

        return new PageResult<>(data, total, pageNum, pageSize);
    }

    /**
     * 分页查询返回Map列表
     */
    public PageResult<Map<String, Object>> queryForPageMap(String sql, int pageNum, int pageSize, Object... params) {
        if (pageNum < 1)
            pageNum = 1;
        if (pageSize < 1)
            pageSize = 10;

        int offset = (pageNum - 1) * pageSize;
        String pageSql = sql + " LIMIT " + pageSize + " OFFSET " + offset;

        List<Map<String, Object>> data = queryForMapList(pageSql, params);

        String countSql = buildCountSql(sql);
        Object totalObj = queryForValue(countSql, params);
        long total = totalObj != null ? ((Number) totalObj).longValue() : 0;

        return new PageResult<>(data, total, pageNum, pageSize);
    }

    /**
     * 执行事务操作
     *
     * @param operations 事务操作列表
     * @return 是否成功
     */
    public boolean executeTransaction(List<TransactionOperation> operations) {
        if (operations == null || operations.isEmpty()) {
            return true;
        }

        Connection connection = null;
        try {
            connection = HikariPoolUtil.getConnection();
            connection.setAutoCommit(false);

            for (TransactionOperation operation : operations) {
                try (PreparedStatement pstmt = connection.prepareStatement(operation.getSql())) {
                    setParameters(pstmt, operation.getParams());
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
            log.debug("事务执行成功，共 {} 个操作", operations.size());
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    log.debug("事务已回滚");
                } catch (SQLException ex) {
                    log.error("事务回滚失败", ex);
                }
            }
            log.error("事务执行失败: {}", e.getMessage(), e);
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    log.error("关闭连接失败", e);
                }
            }
        }
    }

    /**
     * 根据ID删除记录
     *
     * @param tableName 表名
     * @param idColumn  ID列名
     * @param id        ID值
     * @return 是否删除成功
     */
    public boolean deleteById(String tableName, String idColumn, Object id) {
        if (tableName == null || tableName.trim().isEmpty() ||
                idColumn == null || idColumn.trim().isEmpty() || id == null) {
            return false;
        }

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
        return update(sql, id) > 0;
    }

    /**
     * 根据条件删除记录
     *
     * @param tableName 表名
     * @param condition 条件（如：name = ? AND age > ?）
     * @param params    参数
     * @return 删除的行数
     */
    public int deleteByCondition(String tableName, String condition, Object... params) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return 0;
        }

        String sql = "DELETE FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }

        return update(sql, params);
    }

    /**
     * 根据ID查询单条记录
     *
     * @param tableName 表名
     * @param idColumn  ID列名
     * @param id        ID值
     * @param clazz     目标类
     * @param <T>       泛型
     * @return 查询结果
     */
    public <T> T findById(String tableName, String idColumn, Object id, Class<T> clazz) {
        if (tableName == null || tableName.trim().isEmpty() ||
                idColumn == null || idColumn.trim().isEmpty() ||
                id == null || clazz == null) {
            return null;
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";
        return queryForObject(sql, clazz, id);
    }

    /**
     * 构建COUNT查询SQL
     */
    private String buildCountSql(String originalSql) {
        String upperSql = originalSql.toUpperCase().trim();

        // 简单的COUNT SQL构建，实际项目中可能需要更复杂的解析
        if (upperSql.contains("ORDER BY")) {
            int orderByIndex = upperSql.lastIndexOf("ORDER BY");
            originalSql = originalSql.substring(0, orderByIndex).trim();
        }

        return "SELECT COUNT(*) FROM (" + originalSql + ") AS count_query";
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
                                    Object convertedValue = convertValue(columnValue,
                                            setterMethod.getParameterTypes()[0]);
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

    /**
     * 获取表的总记录数
     *
     * @param tableName 表名
     * @return 记录总数
     */
    public long count(String tableName) {
        return count(tableName, null);
    }

    /**
     * 根据条件获取表的记录数
     *
     * @param tableName 表名
     * @param condition 条件（如：age > ? AND name LIKE ?）
     * @param params    参数
     * @return 记录数
     */
    public long count(String tableName, String condition, Object... params) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return 0;
        }

        String sql = "SELECT COUNT(*) FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }

        Object result = queryForValue(sql, params);
        return result != null ? ((Number) result).longValue() : 0;
    }

    /**
     * 查询指定条件的记录列表
     *
     * @param tableName 表名
     * @param clazz     目标类
     * @param condition 条件（如：age > ? AND name LIKE ?）
     * @param params    参数
     * @param <T>       泛型
     * @return 查询结果列表
     */
    public <T> List<T> findByCondition(String tableName, Class<T> clazz, String condition, Object... params) {
        if (tableName == null || tableName.trim().isEmpty() || clazz == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }

        return queryForList(sql, clazz, params);
    }

    /**
     * 查询指定条件的单条记录
     *
     * @param tableName 表名
     * @param clazz     目标类
     * @param condition 条件（如：name = ? AND age > ?）
     * @param params    参数
     * @param <T>       泛型
     * @return 查询结果，如果没有找到则返回null
     */
    public <T> T findOneByCondition(String tableName, Class<T> clazz, String condition, Object... params) {
        List<T> list = findByCondition(tableName, clazz, condition, params);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }

    /**
     * 查询所有记录
     *
     * @param tableName 表名
     * @param clazz     目标类
     * @param <T>       泛型
     * @return 所有记录列表
     */
    public <T> List<T> findAll(String tableName, Class<T> clazz) {
        return findByCondition(tableName, clazz, null);
    }

    /**
     * 带排序的查询
     *
     * @param tableName 表名
     * @param clazz     目标类
     * @param condition 条件
     * @param orderBy   排序条件（如：name ASC, age DESC）
     * @param params    参数
     * @param <T>       泛型
     * @return 查询结果列表
     */
    public <T> List<T> findByConditionWithOrder(String tableName, Class<T> clazz, String condition, String orderBy,
            Object... params) {
        if (tableName == null || tableName.trim().isEmpty() || clazz == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }
        if (orderBy != null && !orderBy.trim().isEmpty()) {
            sql += " ORDER BY " + orderBy;
        }

        return queryForList(sql, clazz, params);
    }

    /**
     * 限制结果数量的查询
     *
     * @param tableName 表名
     * @param clazz     目标类
     * @param condition 条件
     * @param limit     限制数量
     * @param params    参数
     * @param <T>       泛型
     * @return 查询结果列表
     */
    public <T> List<T> findByConditionWithLimit(String tableName, Class<T> clazz, String condition, int limit,
            Object... params) {
        if (tableName == null || tableName.trim().isEmpty() || clazz == null) {
            return new ArrayList<>();
        }

        String sql = "SELECT * FROM " + tableName;
        if (condition != null && !condition.trim().isEmpty()) {
            sql += " WHERE " + condition;
        }
        sql += " LIMIT " + limit;

        return queryForList(sql, clazz, params);
    }

    /**
     * 更新指定ID的记录
     *
     * @param tableName 表名
     * @param updates   更新的字段和值的映射
     * @param idColumn  ID列名
     * @param id        ID值
     * @return 是否更新成功
     */
    public boolean updateById(String tableName, Map<String, Object> updates, String idColumn, Object id) {
        if (tableName == null || tableName.trim().isEmpty() ||
                updates == null || updates.isEmpty() ||
                idColumn == null || idColumn.trim().isEmpty() || id == null) {
            return false;
        }

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        List<Object> params = new ArrayList<>();

        boolean first = true;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            params.add(entry.getValue());
            first = false;
        }

        sql.append(" WHERE ").append(idColumn).append(" = ?");
        params.add(id);

        return update(sql.toString(), params.toArray()) > 0;
    }

    /**
     * 根据条件更新记录
     *
     * @param tableName 表名
     * @param updates   更新的字段和值的映射
     * @param condition 条件
     * @param params    条件参数
     * @return 更新的行数
     */
    public int updateByCondition(String tableName, Map<String, Object> updates, String condition, Object... params) {
        if (tableName == null || tableName.trim().isEmpty() ||
                updates == null || updates.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        List<Object> allParams = new ArrayList<>();

        boolean first = true;
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            if (!first) {
                sql.append(", ");
            }
            sql.append(entry.getKey()).append(" = ?");
            allParams.add(entry.getValue());
            first = false;
        }

        if (condition != null && !condition.trim().isEmpty()) {
            sql.append(" WHERE ").append(condition);
            if (params != null) {
                for (Object param : params) {
                    allParams.add(param);
                }
            }
        }

        return update(sql.toString(), allParams.toArray());
    }

    /**
     * 将object里的数据插入到数据表中
     * 
     * @param tableName 表名
     * @param object    要插入的对象
     * @return 是否插入成功
     */
    public boolean insert(String tableName, Object object) {
        if (tableName == null || tableName.trim().isEmpty() || object == null) {
            log.warn("表名和对象不能为空");
            return false;
        }

        try {
            // 获取对象的所有getter方法
            Map<String, Object> fieldValues = extractFieldValues(object);

            if (fieldValues.isEmpty()) {
                log.warn("对象中没有找到可用的属性值");
                return false;
            }

            // 使用已有的Map插入方法
            return insert(tableName, fieldValues);

        } catch (Exception e) {
            log.error("基于对象插入数据失败，表名: {}, 对象类型: {}, 错误信息: {}",
                    tableName, object.getClass().getSimpleName(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 简单的插入操作（基于Map）
     *
     * @param tableName 表名
     * @param data      数据映射
     * @return 是否插入成功
     */
    public boolean insert(String tableName, Map<String, Object> data) {
        if (tableName == null || tableName.trim().isEmpty() ||
                data == null || data.isEmpty()) {
            return false;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<Object> params = new ArrayList<>();

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                sql.append(", ");
                values.append(", ");
            }
            sql.append(entry.getKey());
            values.append("?");
            params.add(entry.getValue());
            first = false;
        }

        sql.append(")").append(values).append(")");

        return update(sql.toString(), params.toArray()) > 0;
    }

    /**
     * 插入并返回生成的主键（基于Map）
     *
     * @param tableName 表名
     * @param data      数据映射
     * @return 生成的主键，如果失败则返回null
     */
    public Object insertAndGetKey(String tableName, Map<String, Object> data) {
        if (tableName == null || tableName.trim().isEmpty() ||
                data == null || data.isEmpty()) {
            return null;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<Object> params = new ArrayList<>();

        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                sql.append(", ");
                values.append(", ");
            }
            sql.append(entry.getKey());
            values.append("?");
            params.add(entry.getValue());
            first = false;
        }

        sql.append(")").append(values).append(")");

        return insertAndReturnKey(sql.toString(), params.toArray());
    }

    /**
     * 将对象插入并返回生成的主键
     * 
     * @param tableName 表名
     * @param object    要插入的对象
     * @return 生成的主键，如果失败则返回null
     */
    public Object insertAndGetKey(String tableName, Object object) {
        if (tableName == null || tableName.trim().isEmpty() || object == null) {
            log.warn("表名和对象不能为空");
            return null;
        }

        try {
            // 获取对象的所有属性值
            Map<String, Object> fieldValues = extractFieldValues(object);

            if (fieldValues.isEmpty()) {
                log.warn("对象中没有找到可用的属性值");
                return null;
            }

            // 使用已有的Map插入方法
            return insertAndGetKey(tableName, fieldValues);

        } catch (Exception e) {
            log.error("基于对象插入数据并获取主键失败，表名: {}, 对象类型: {}, 错误信息: {}",
                    tableName, object.getClass().getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 提取对象的属性值，通过getter方法获取
     * 
     * @param object 目标对象
     * @return 属性名和值的映射
     */
    private Map<String, Object> extractFieldValues(Object object) {
        Map<String, Object> fieldValues = new HashMap<>();

        if (object == null) {
            return fieldValues;
        }

        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            String methodName = method.getName();

            // 只处理getter方法
            if (isGetterMethod(method)) {
                try {
                    Object value = method.invoke(object);

                    // 跳过null值和ID字段（通常是自增主键）
                    if (value != null && !isIdField(methodName)) {
                        String fieldName = getFieldNameFromGetter(methodName);
                        String columnName = camelToSnake(fieldName);
                        fieldValues.put(columnName, value);
                    }

                } catch (Exception e) {
                    log.debug("获取属性值失败: {}, 方法: {}", e.getMessage(), methodName);
                }
            }
        }

        return fieldValues;
    }

    /**
     * 判断是否为getter方法
     * 
     * @param method 方法对象
     * @return 是否为getter方法
     */
    private boolean isGetterMethod(Method method) {
        String methodName = method.getName();
        if ("getClass".equals(method.getName())) {
            return false;
        }
        // 必须是public方法
        if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
            return false;
        }

        // 没有参数
        if (method.getParameterCount() != 0) {
            return false;
        }

        // 有返回值（非void）
        if (method.getReturnType() == void.class) {
            return false;
        }

        // 方法名符合getter规范
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return true;
        }

        // 对于boolean类型，可能是is开头
        if (methodName.startsWith("is") && methodName.length() > 2 &&
                (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
            return true;
        }

        return false;
    }

    /**
     * 判断是否为ID字段（通常不需要插入，因为是自增主键）
     * 
     * @param methodName getter方法名
     * @return 是否为ID字段
     */
    private boolean isIdField(String methodName) {
        String fieldName = getFieldNameFromGetter(methodName).toLowerCase();
        // 只跳过主键ID字段，不跳过外键ID字段如user_id
        return "id".equals(fieldName) || "equipmentid".equals(fieldName);
    }

    /**
     * 从getter方法名获取字段名
     * 
     * @param methodName getter方法名
     * @return 字段名
     */
    private String getFieldNameFromGetter(String methodName) {
        if (methodName.startsWith("get") && methodName.length() > 3) {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        } else if (methodName.startsWith("is") && methodName.length() > 2) {
            return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        }
        return methodName;
    }

    /**
     * 将驼峰命名转换为下划线命名
     * 例如: userName -> user_name
     * 
     * @param camelCase 驼峰命名的字符串
     * @return 下划线命名的字符串
     */
    private String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
