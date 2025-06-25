package com.xszx.util.db.examples;

import com.xszx.util.db.JDBCTemplate;

/**
 * 演示基于对象插入功能的示例
 */
public class ObjectInsertExample {

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
            this.createTime = java.time.LocalDateTime.now();
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
            return "User{id=" + id + ", name='" + name + "', email='" + email +
                    "', age=" + age + ", createTime=" + createTime + "}";
        }
    }

    /**
     * 演示对象插入功能
     */
    public static void demonstrateObjectInsert() {
        JDBCTemplate jdbcTemplate = new JDBCTemplate();

        System.out.println("=== 基于对象的插入操作演示 ===");

        // 创建用户对象
        User user1 = new User("张三", "zhangsan@example.com", 25);
        User user2 = new User("李四", "lisi@example.com", 30);
        User user3 = new User("王五", "wangwu@example.com", 28);

        System.out.println("\n1. 基于对象的插入操作:");

        // 方式1: insert方法（不返回主键）
        boolean success1 = jdbcTemplate.insert("users", user1);
        System.out.println("插入用户1结果: " + success1);
        System.out.println("插入的用户: " + user1);

        // 方式2: insertAndGetKey方法（返回生成的主键）
        Object generatedId2 = jdbcTemplate.insertAndGetKey("users", user2);
        System.out.println("插入用户2，生成的主键: " + generatedId2);
        System.out.println("插入的用户: " + user2);

        Object generatedId3 = jdbcTemplate.insertAndGetKey("users", user3);
        System.out.println("插入用户3，生成的主键: " + generatedId3);
        System.out.println("插入的用户: " + user3);

        System.out.println("\n2. 验证插入结果:");

        // 查询验证
        long totalUsers = jdbcTemplate.count("users");
        System.out.println("用户总数: " + totalUsers);

        // 查询刚插入的用户
        if (generatedId2 != null) {
            User queryUser = jdbcTemplate.findById("users", "id", generatedId2, User.class);
            System.out.println("查询到的用户2: " + queryUser);
        }

        if (generatedId3 != null) {
            User queryUser = jdbcTemplate.findById("users", "id", generatedId3, User.class);
            System.out.println("查询到的用户3: " + queryUser);
        }
    }

    /**
     * 演示复杂对象的插入
     */
    public static class Product {
        private Long productId;
        private String productName;
        private String description;
        private Double price;
        private Integer stock;
        private Boolean isActive;
        private java.time.LocalDate createDate;

        public Product() {
        }

        public Product(String productName, String description, Double price, Integer stock) {
            this.productName = productName;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.isActive = true;
            this.createDate = java.time.LocalDate.now();
        }

        // Getters and Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public java.time.LocalDate getCreateDate() {
            return createDate;
        }

        public void setCreateDate(java.time.LocalDate createDate) {
            this.createDate = createDate;
        }

        @Override
        public String toString() {
            return "Product{productId=" + productId + ", productName='" + productName +
                    "', description='" + description + "', price=" + price +
                    ", stock=" + stock + ", isActive=" + isActive + ", createDate=" + createDate + "}";
        }
    }

    /**
     * 演示复杂对象插入
     */
    public static void demonstrateComplexObjectInsert() {
        JDBCTemplate jdbcTemplate = new JDBCTemplate();

        System.out.println("\n=== 复杂对象插入演示 ===");

        // 创建产品对象
        Product product1 = new Product("笔记本电脑", "高性能游戏本", 8999.99, 50);
        Product product2 = new Product("智能手机", "5G全网通", 3299.00, 100);

        // 插入产品（假设有products表）
        // 注意：这里需要根据实际的表结构来调整
        try {
            Object productId1 = jdbcTemplate.insertAndGetKey("products", product1);
            System.out.println("插入产品1，生成ID: " + productId1);
            System.out.println("产品1详情: " + product1);

            Object productId2 = jdbcTemplate.insertAndGetKey("products", product2);
            System.out.println("插入产品2，生成ID: " + productId2);
            System.out.println("产品2详情: " + product2);

        } catch (Exception e) {
            System.out.println("插入产品失败（可能是因为products表不存在）: " + e.getMessage());
        }
    }

    /**
     * 演示字段名映射
     */
    public static void demonstrateFieldMapping() {
        System.out.println("\n=== 字段名映射演示 ===");
        System.out.println("Java对象属性 -> 数据库字段名映射:");
        System.out.println("id -> id (ID字段会被自动跳过)");
        System.out.println("name -> name");
        System.out.println("email -> email");
        System.out.println("age -> age");
        System.out.println("createTime -> create_time");
        System.out.println("productId -> product_id (ID字段会被自动跳过)");
        System.out.println("productName -> product_name");
        System.out.println("isActive -> is_active");
        System.out.println("createDate -> create_date");
    }

    public static void main(String[] args) {
        try {
            // 演示基本对象插入
            demonstrateObjectInsert();

            // 演示复杂对象插入
            demonstrateComplexObjectInsert();

            // 演示字段映射规则
            demonstrateFieldMapping();

            System.out.println("\n=== 对象插入功能演示完成 ===");

        } catch (Exception e) {
            System.err.println("演示过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
