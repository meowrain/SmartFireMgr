# JDBCTemplate 完整功能说明

## 概述

JDBCTemplate 是一个功能完整的数据库操作模板类，提供了便捷的增删改查功能，支持 ORM 映射、分页查询、事务管理、批量操作等特性。

## 主要特性

### 1. 基础 CRUD 操作

#### 查询操作

- `queryForList(sql, clazz, params...)` - 查询多条记录并映射到对象列表
- `queryForObject(sql, clazz, params...)` - 查询单条记录并映射到对象
- `queryForValue(sql, params...)` - 查询单个值（如聚合函数结果）
- `queryForMapList(sql, params...)` - 查询并返回 Map 列表

#### 更新操作

- `update(sql, params...)` - 执行更新操作（INSERT、UPDATE、DELETE）
- `insertAndReturnKey(sql, params...)` - 插入数据并返回生成的主键
- `batchUpdate(sql, batchParams)` - 批量更新操作
- `batchInsert(sql, batchParams)` - 批量插入操作（事务安全）

### 2. 便捷的表操作方法

#### 查询方法

- `findById(tableName, idColumn, id, clazz)` - 根据 ID 查询单条记录
- `findAll(tableName, clazz)` - 查询表中所有记录
- `findByCondition(tableName, clazz, condition, params...)` - 根据条件查询
- `findOneByCondition(tableName, clazz, condition, params...)` - 根据条件查询单条记录
- `findByConditionWithOrder(tableName, clazz, condition, orderBy, params...)` - 带排序的条件查询
- `findByConditionWithLimit(tableName, clazz, condition, limit, params...)` - 限制数量的查询

#### 插入方法

- `insert(tableName, dataMap)` - 基于 Map 的插入操作
- `insertAndGetKey(tableName, dataMap)` - 插入并返回主键（Map 方式）

#### 更新方法

- `updateById(tableName, updates, idColumn, id)` - 根据 ID 更新记录
- `updateByCondition(tableName, updates, condition, params...)` - 根据条件更新记录

#### 删除方法

- `deleteById(tableName, idColumn, id)` - 根据 ID 删除记录
- `deleteByCondition(tableName, condition, params...)` - 根据条件删除记录

### 3. 分页查询支持

- `queryForPage(sql, clazz, pageNum, pageSize, params...)` - 对象分页查询
- `queryForPageMap(sql, pageNum, pageSize, params...)` - Map 分页查询
- 返回 `PageResult<T>` 对象，包含数据、总数、页码等完整分页信息

### 4. 事务管理

- `executeTransaction(operations)` - 执行事务操作列表
- 支持回滚机制，确保数据一致性
- 使用 `TransactionOperation` 封装事务中的每个操作

### 5. 统计和聚合功能

- `count(tableName)` - 统计表记录总数
- `count(tableName, condition, params...)` - 根据条件统计记录数
- `exists(sql, params...)` - 检查记录是否存在

### 6. 性能优化特性

- **setter 方法缓存**：避免重复反射操作
- **连接池支持**：使用 HikariCP 连接池
- **批量操作优化**：支持大数据量的批处理
- **预编译语句**：防止 SQL 注入，提升性能

### 7. 类型转换支持

自动处理以下类型转换：

- 基本类型和包装类型（int, Integer, long, Long 等）
- 字符串类型
- 日期时间类型（LocalDate, LocalDateTime）
- 大数字类型（BigDecimal）
- 布尔类型

### 8. 命名约定转换

- **数据库字段**：支持下划线命名（snake_case）
- **Java 属性**：自动转换为驼峰命名（camelCase）
- 例如：`user_name` → `userName`

## 使用示例

### 基本查询

```java
JDBCTemplate template = new JDBCTemplate();

// 查询所有用户
List<User> users = template.findAll("users", User.class);

// 根据条件查询
List<User> youngUsers = template.findByCondition("users", User.class, "age < ?", 30);

// 分页查询
PageResult<User> page = template.queryForPage("SELECT * FROM users", User.class, 1, 10);
```

### 插入操作

```java
// 方式1：原生SQL
Object userId = template.insertAndReturnKey(
    "INSERT INTO users (name, email, age) VALUES (?, ?, ?)",
    "张三", "zhangsan@example.com", 25
);

// 方式2：Map方式
Map<String, Object> userData = new HashMap<>();
userData.put("name", "李四");
userData.put("email", "lisi@example.com");
userData.put("age", 30);
Object userId2 = template.insertAndGetKey("users", userData);
```

### 更新操作

```java
// 根据ID更新
Map<String, Object> updates = new HashMap<>();
updates.put("age", 26);
updates.put("email", "new_email@example.com");
boolean success = template.updateById("users", updates, "id", userId);

// 原生SQL更新
int rows = template.update("UPDATE users SET age = age + 1 WHERE age < ?", 30);
```

### 事务操作

```java
List<TransactionOperation> operations = Arrays.asList(
    new TransactionOperation("INSERT INTO users (name, email) VALUES (?, ?)", "用户1", "user1@example.com"),
    new TransactionOperation("INSERT INTO users (name, email) VALUES (?, ?)", "用户2", "user2@example.com"),
    new TransactionOperation("UPDATE users SET age = ? WHERE name = ?", 25, "用户1")
);

boolean success = template.executeTransaction(operations);
```

### 批量操作

```java
// 批量插入
List<Object[]> batchData = Arrays.asList(
    new Object[]{"用户1", "user1@example.com", 25},
    new Object[]{"用户2", "user2@example.com", 30},
    new Object[]{"用户3", "user3@example.com", 28}
);

int insertedCount = template.batchInsert(
    "INSERT INTO users (name, email, age) VALUES (?, ?, ?)",
    batchData
);
```

## 依赖要求

- Java 8+
- SLF4J 日志框架
- HikariCP 连接池
- MySQL/PostgreSQL 等数据库驱动

## 注意事项

1. 目标类必须有无参构造器
2. 属性需要对应的 setter 方法
3. 数据库连接池需要正确配置
4. SQL 语句中的参数使用 `?` 占位符
5. 表名和列名建议使用下划线命名规范

这个 JDBCTemplate 类现在提供了完整的增删改查功能，支持各种复杂场景，是一个功能强大且易用的数据库操作工具。
