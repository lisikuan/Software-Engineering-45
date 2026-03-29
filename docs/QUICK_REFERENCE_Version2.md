# 快速参考卡片

## DAO 层文件清单

### ✅ 已完成
```
src/main/java/edu/bupt/tarecruitment/
├── model/
│   ├── User.java ✅
│   ├── Student.java ✅
│   ├── Job.java ✅
│   └── Application.java ✅
├── common/
│   ├── FileUtil.java ✅
│   ├── ValidationUtil.java ✅
│   ├── DateUtil.java ✅
│   └── exception/
│       ├── DataAccessException.java ✅
│       ├── ValidationException.java ✅
│       └── BusinessException.java ✅
└── persistence/
    ├── repository/
    │   ├── UserRepository.java ✅
    │   ├── StudentRepository.java ✅
    │   ├── JobRepository.java ✅
    │   └── ApplicationRepository.java ✅
    └── json/
        └── JsonUserRepository.java ✅

src/test/java/edu/bupt/tarecruitment/
└── persistence/json/
    └── JsonUserRepositoryTest.java ✅ (18 tests)
```

### ⏳ 待实现
```
src/main/java/edu/bupt/tarecruitment/persistence/json/
├── JsonStudentRepository.java ⏳
├── JsonJobRepository.java ⏳
└── JsonApplicationRepository.java ⏳

src/test/java/edu/bupt/tarecruitment/
├── persistence/json/
│   ├── JsonStudentRepositoryTest.java ⏳
│   ├── JsonJobRepositoryTest.java ⏳
│   └── JsonApplicationRepositoryTest.java ⏳
└── common/
    ├── ValidationUtilTest.java ⏳
    └── FileUtilTest.java ⏳
```

## 常用代码片段

### 创建新实体
```java
Entity entity = new Entity();
entity.setId(null); // 让 Repository 生成 ID
entity.setCreatedAt(null); // 让 Repository 设置时间
repository.create(entity);
```

### 查询操作
```java
// 单个查询
Optional<Entity> entity = repository.findById("id");
if (entity.isPresent()) {
    // 处理
}

// 列表查询
List<Entity> list = repository.findAll();
List<Entity> filtered = list.stream()
    .filter(e -> condition)
    .toList();
```

### 更新操作
```java
Optional<Entity> entity = repository.findById("id");
if (entity.isPresent()) {
    Entity e = entity.get();
    e.setField("newValue");
    repository.update(e);
}
```

### 删除操作
```java
boolean deleted = repository.delete("id");
if (deleted) {
    // 成功删除
}
```

### 验证数据
```java
try {
    ValidationUtil.validateEmail(email);
    ValidationUtil.validateUsername(username);
    ValidationUtil.validatePassword(password);
} catch (ValidationException e) {
    // 处理验证失败
}
```

### 异常处理
```java
try {
    repository.create(entity);
} catch (ValidationException e) {
    // 数据验证失败
    logger.warn("Validation failed: " + e.getMessage());
} catch (BusinessException e) {
    // 业务规则违反
    logger.error("Business error: " + e.getMessage());
} catch (DataAccessException e) {
    // 文件I/O失败
    logger.error("Data access failed: " + e.getMessage(), e);
}
```

## 测试模板

```java
@Test
@DisplayName("描述测试的目的")
void testMethodName() {
    // Arrange - 设置测试数据
    Entity entity = new Entity();
    entity.setField("value");
    
    // Act - 执行操作
    Entity result = repository.create(entity);
    
    // Assert - 验证结果
    assertNotNull(result);
    assertEquals("value", result.getField());
}
```

## 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | PascalCase | UserRepository |
| 方法名 | camelCase | findById() |
| 常量 | UPPER_SNAKE_CASE | DATA_FILE |
| 变量 | camelCase | userName |
| 包名 | lowercase | edu.bupt.tarecruitment |
| 接口 | Interface + 名词 | UserRepository |
| 实现 | [Type] + Repository + 前缀 | JsonUserRepository |

## JSON 字段映射

| Java 类型 | JSON 类型 | 示例 |
|-----------|-----------|------|
| String | string | "value" |
| int | number | 42 |
| LocalDateTime | string (ISO) | "2024-01-01T10:00:00" |
| enum | string | "OPEN" |
| List<String> | array | ["a", "b"] |
| boolean | boolean | true |
| null | null | null |

## 常见错误和解决方案

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| JSON 反序列化失败 | LocalDateTime 格式错误 | 注册 JavaTimeModule |
| 重复 ID | 没有检查唯一性 | 在 create() 中检查 |
| 文件不存在异常 | 目录不存在 | 调用 ensureDirectoryExists() |
| 数据未保存 | 没有调用 writeJsonArray() | 创建后必须调用写入 |
| 部分写入 | 直接修改原文件 | 使用临时文件方式 |
| ClassCastException | 类型转换错误 | 使用正确的类型参数 |
| ConcurrentModificationException | 遍历时修改列表 | 使用 removeIf() 或迭代器 |

## 性能优化建议

1. **缓存考虑** [待确认]
   - 频繁查询的数据可以缓存
   - 注意缓存失效问题

2. **批量操作优化**
   - 减少文件 I/O 次数
   - 合并多个写操作

3. **大文件处理** [待确认]
   - 当 JSON 文件很大时性能下降
   - 可考虑分片或分页

## 调试技巧

```java
// 打印 JSON 文件内容
List<Entity> list = FileUtil.readJsonArray("data/file.json", Entity.class);
System.out.println("Data: " + list);

// 检查文件是否存在
if (FileUtil.fileExists("data/file.json")) {
    System.out.println("File exists");
}

// 捕获异常堆栈
} catch (Exception e) {
    e.printStackTrace();
    logger.error("Error occurred", e);
}
```

## 相关链接

- AGENTS.md - 架构和编码规范
- IMPLEMENTATION_GUIDE.md - 详细实现指南
- DATA_FLOW_DIAGRAM.md - 数据流图
- NEXT_STEPS.md - 后续任务计划

---

**下一步**: 执行任务 2.1 - 实现 JsonStudentRepository