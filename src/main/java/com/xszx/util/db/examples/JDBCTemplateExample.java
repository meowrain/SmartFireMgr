package com.xszx.util.db.examples;

import com.xszx.util.db.JDBCTemplate;
import com.xszx.util.db.PageResult;
import com.xszx.util.db.TransactionOperation;

import java.util.*;

/**
 * JDBCTemplate使用示例
 */
public class JDBCTemplateExample {

    private JDBCTemplate jdbcTemplate = new JDBCTemplate();

    /**
     * 示例用户类
     */
    public static class User {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        private java.time.LocalDateTime createTime;

        // 构造器
        public User() {
        }

        public User(String name, String email, Integer age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public java.time.LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(java.time.LocalDateTime createTime) {
            this.createTime = createTime;
        }

        @Override
        public String toString() {
            return "User{id=" + id + ", name='" + name + "', email='" + email + "', age=" + age + ", createTime="
                    + createTime + "}";
        }
    }

    /**
     * 演示基本的增删改查操作
     */
    public void demonstrateBasicCRUD() {
        System.out.println("=== 基本CRUD操作演示 ===");

        // 1. 插入操作
        System.out.println("\n1. 插入操作:");

        // 方式1: 使用原生SQL
        String insertSql = "INSERT INTO users (name, email, age, create_time) VALUES (?, ?, ?, NOW())";
        Object userId = jdbcTemplate.insertAndReturnKey(insertSql, "张三", "zhangsan@example.com", 25);
        System.out.println("插入用户，生成ID: " + userId);

        // 方式2: 使用Map方式插入
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "李四");
        userData.put("email", "lisi@example.com");
        userData.put("age", 30);
        userData.put("create_time", java.time.LocalDateTime.now());

        Object userId2 = jdbcTemplate.insertAndGetKey("users", userData);
        System.out.println("插入用户（Map方式），生成ID: " + userId2);

        // 2. 查询操作
        System.out.println("\n2. 查询操作:");

        // 根据ID查询
        User user = jdbcTemplate.findById("users", "id", userId, User.class);
        System.out.println("根据ID查询: " + user);

        // 查询所有用户
        List<User> allUsers = jdbcTemplate.findAll("users", User.class);
        System.out.println("所有用户: " + allUsers);

        // 条件查询
        List<User> youngUsers = jdbcTemplate.findByCondition("users", User.class, "age < ?", 28);
        System.out.println("年龄小于28的用户: " + youngUsers);

        // 带排序的查询
        List<User> sortedUsers = jdbcTemplate.findByConditionWithOrder("users", User.class,
                "age > ?", "age DESC", 20);
        System.out.println("年龄大于20的用户（按年龄降序）: " + sortedUsers);

        // 3. 更新操作
        System.out.println("\n3. 更新操作:");

        // 根据ID更新
        Map<String, Object> updates = new HashMap<>();
        updates.put("age", 26);
        updates.put("email", "zhangsan_new@example.com");

        boolean updateSuccess = jdbcTemplate.updateById("users", updates, "id", userId);
        System.out.println("更新用户成功: " + updateSuccess);

        // 根据条件更新
        Map<String, Object> ageUpdate = new HashMap<>();
        ageUpdate.put("age", 31);

        int updatedRows = jdbcTemplate.updateByCondition("users", ageUpdate, "name = ?", "李四");
        System.out.println("批量更新影响行数: " + updatedRows);

        // 4. 删除操作
        System.out.println("\n4. 删除操作:");

        // 根据ID删除
        boolean deleteSuccess = jdbcTemplate.deleteById("users", "id", userId);
        System.out.println("删除用户成功: " + deleteSuccess);

        // 根据条件删除
        int deletedRows = jdbcTemplate.deleteByCondition("users", "age > ?", 30);
        System.out.println("条件删除影响行数: " + deletedRows);
    }

    /**
     * 演示分页查询
     */
    public void demonstratePagination() {
        System.out.println("\n=== 分页查询演示 ===");

        // 先插入一些测试数据
        for (int i = 1; i <= 20; i++) {
            Map<String, Object> user = new HashMap<>();
            user.put("name", "用户" + i);
            user.put("email", "user" + i + "@example.com");
            user.put("age", 20 + (i % 10));
            user.put("create_time", java.time.LocalDateTime.now());
            jdbcTemplate.insert("users", user);
        }

        // 分页查询
        String sql = "SELECT * FROM users WHERE age > ?";
        PageResult<User> page1 = jdbcTemplate.queryForPage(sql, User.class, 1, 5, 22);

        System.out.println("第1页数据:");
        System.out.println("总记录数: " + page1.getTotal());
        System.out.println("总页数: " + page1.getTotalPages());
        System.out.println("当前页: " + page1.getPageNum());
        System.out.println("数据: " + page1.getData());

        // 查询第2页
        PageResult<User> page2 = jdbcTemplate.queryForPage(sql, User.class, 2, 5, 22);
        System.out.println("\n第2页数据: " + page2.getData());
    }

    /**
     * 演示事务操作
     */
    public void demonstrateTransaction() {
        System.out.println("\n=== 事务操作演示 ===");

        // 准备事务操作
        List<TransactionOperation> operations = new ArrayList<>();

        // 操作1: 插入用户
        operations.add(new TransactionOperation(
                "INSERT INTO users (name, email, age, create_time) VALUES (?, ?, ?, NOW())",
                "事务用户1", "trans1@example.com", 25));

        // 操作2: 插入另一个用户
        operations.add(new TransactionOperation(
                "INSERT INTO users (name, email, age, create_time) VALUES (?, ?, ?, NOW())",
                "事务用户2", "trans2@example.com", 30));

        // 操作3: 更新第一个用户的年龄
        operations.add(new TransactionOperation(
                "UPDATE users SET age = ? WHERE name = ?",
                26, "事务用户1"));

        // 执行事务
        boolean transactionSuccess = jdbcTemplate.executeTransaction(operations);
        System.out.println("事务执行结果: " + transactionSuccess);
    }

    /**
     * 演示批量操作
     */
    public void demonstrateBatchOperations() {
        System.out.println("\n=== 批量操作演示 ===");

        // 批量插入
        String batchInsertSql = "INSERT INTO users (name, email, age, create_time) VALUES (?, ?, ?, NOW())";
        List<Object[]> batchData = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            batchData.add(new Object[] { "批量用户" + i, "batch" + i + "@example.com", 20 + i });
        }

        int insertedCount = jdbcTemplate.batchInsert(batchInsertSql, batchData);
        System.out.println("批量插入成功: " + insertedCount + " 条记录");

        // 批量更新
        String batchUpdateSql = "UPDATE users SET age = age + 1 WHERE name = ?";
        List<Object[]> updateData = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            updateData.add(new Object[] { "批量用户" + i });
        }

        int[] updateResults = jdbcTemplate.batchUpdate(batchUpdateSql, updateData);
        System.out.println("批量更新结果: " + Arrays.toString(updateResults));
    }

    /**
     * 演示聚合查询和统计
     */
    public void demonstrateAggregateQueries() {
        System.out.println("\n=== 聚合查询演示 ===");

        // 统计总数
        long totalUsers = jdbcTemplate.count("users");
        System.out.println("用户总数: " + totalUsers);

        // 条件统计
        long adultUsers = jdbcTemplate.count("users", "age >= ?", 18);
        System.out.println("成年用户数: " + adultUsers);

        // 查询最大年龄
        Object maxAge = jdbcTemplate.queryForValue("SELECT MAX(age) FROM users");
        System.out.println("最大年龄: " + maxAge);

        // 查询平均年龄
        Object avgAge = jdbcTemplate.queryForValue("SELECT AVG(age) FROM users");
        System.out.println("平均年龄: " + avgAge);

        // 检查记录是否存在
        boolean exists = jdbcTemplate.exists("SELECT 1 FROM users WHERE email = ?", "zhangsan@example.com");
        System.out.println("邮箱是否存在: " + exists);
    }

    /**
     * 演示Map查询
     */
    public void demonstrateMapQueries() {
        System.out.println("\n=== Map查询演示 ===");

        // 查询返回Map列表
        List<Map<String, Object>> mapResults = jdbcTemplate.queryForMapList(
                "SELECT name, email, age FROM users WHERE age > ? LIMIT 3", 25);

        System.out.println("Map查询结果:");
        for (Map<String, Object> row : mapResults) {
            System.out.println(row);
        }

        // 分页Map查询
        PageResult<Map<String, Object>> mapPage = jdbcTemplate.queryForPageMap(
                "SELECT * FROM users WHERE age > ?", 1, 5, 20);

        System.out.println("\nMap分页查询结果:");
        System.out.println("总数: " + mapPage.getTotal());
        System.out.println("数据: " + mapPage.getData());
    }

    /**
     * 主方法，运行所有演示
     */
    public static void main(String[] args) {
        JDBCTemplateExample example = new JDBCTemplateExample();

        try {
            // 检查连接池状态
            if (!example.jdbcTemplate.isConnectionPoolHealthy()) {
                System.err.println("数据库连接池不可用，请检查配置！");
                return;
            }

            System.out.println("数据库连接正常，开始演示...");

            // 运行各种演示
            example.demonstrateBasicCRUD();
            example.demonstratePagination();
            example.demonstrateTransaction();
            example.demonstrateBatchOperations();
            example.demonstrateAggregateQueries();
            example.demonstrateMapQueries();

            System.out.println("\n=== 所有演示完成 ===");

        } catch (Exception e) {
            System.err.println("演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
