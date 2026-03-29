# DAO 层实现指南

## 已完成的工作

### 1. 数据模型 (Model Layer)
✅ 完成创建以下模型类：
- `User.java` - 用户模型，包含角色枚举（STUDENT/ADMIN/TA）
- `Student.java` - 学生模型
- `Job.java` - 岗位模型，包含状态枚举（OPEN/CLOSED/FILLED）
- `Application.java` - 申请模型，包含状态枚举（PENDING/APPROVED/REJECTED/WITHDRAWN）

### 2. 工具类 (Common/Utils)
✅ 完成创建：
- `FileUtil.java` - JSON文件读写工具，支持Jackson序列化/反序列化
  - 读写JSON数组
  - 读写JSON对象
  - 使用临时文件方式防止部分写入
- `ValidationUtil.java` - 验证工具类
  - 邮箱验证、用户名验证、密码强度验证等
  - 字符串长度验证、非空验证
- `DateUtil.java` - 时间工具类
  - ISO 8601 格式转换
  - 时间比较操作

### 3. 异常类 (Exceptions)
✅ 完成创建自定义异常：
- `DataAccessException` - 数据访问异常（文件I/O失败等）
- `ValidationException` - 验证异常
- `BusinessException` - 业务异常

### 4. DAO 层 (Persistence Layer)

#### Repository 接口
✅ 已定义以下接口：
- `UserRepository` - 用户操作接口（10个方法）
- `StudentRepository` - 学生操作接口（9个方法）
- `JobRepository` - 岗位操作接口（8个方法）
- `ApplicationRepository` - 申请操作接口（11个方法）

#### JSON 实现
✅ 已实现：
- `JsonUserRepository` - 完整实现，包含所有验证和错误处理

#### 待实现
⏳ 需要创建以下实现类（按优先级）：
1. `JsonStudentRepository` - 学生数据持久化
2. `JsonJobRepository` - 岗位数据持久化
3. `JsonApplicationRepository` - 申请数据持久化

### 5. 测试
✅ 已创建：
- `JsonUserRepositoryTest` - 18个测试用例，覆盖所有功能

#### 待创建
⏳ 需要为以下类创建测试：
- `JsonStudentRepositoryTest`
- `JsonJobRepositoryTest`
- `JsonApplicationRepositoryTest`
- `ValidationUtilTest`
- `FileUtilTest`

## JSON 数据格式

### users.json
```json
[
  {
    "id": "user_xxxxxxxx",
    "username": "student_001",
    "password": "hashedPassword",
    "role": "STUDENT",
    "email": "student@example.com",
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

### students.json
```json
[
  {
    "id": "student_001",
    "userId": "user_xxxxxxxx",
    "firstName": "John",
    "lastName": "Doe",
    "studentId": "2024001",
    "email": "john@example.com",
    "phone": "1234567890",
    "cvPath": "data/cv/john_cv.pdf",
    "skills": ["Java", "Python"],
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

### jobs.json
```json
[
  {
    "id": "job_001",
    "title": "CS101 Teaching Assistant",
    "courseCode": "CS101",
    "description": "Assist with grading",
    "requiredSkills": ["Java"],
    "maxApplications": 2,
    "status": "OPEN",
    "createdAt": "2024-01-01T10:00:00"
  }
]
```

### applications.json
```json
[
  {
    "id": "app_001",
    "jobId": "job_001",
    "studentId": "student_001",
    "status": "PENDING",
    "appliedAt": "2024-01-05T14:30:00",
    "reviewedAt": null,
    "reviewedBy": null,
    "reviewComment": null
  }
]
```

## 架构遵循 AGENTS.md 规范

✅ 严格分层：
- Presentation → Controller/Service → Repository → JSON Files
- 不允许跨层依赖
- 异常明确定义

✅ 约束遵守：
- 无数据库
- 无Spring框架
- JSON 是唯一的持久化方式
- 状态值使用枚举

## 下一步任务

### 立即完成
1. 实现 `JsonStudentRepository`
2. 实现 `JsonJobRepository`
3. 实现 `JsonApplicationRepository`

### 然后完成
1. 创建所有 Repository 的单元测试
2. 实现 Service 层（业务逻辑）
3. 创建 Controller 层（控制器）
4. 实现前端界面连接

### 测试计划
- Unit Tests: 每个 DAO 方法都需要测试
- Integration Tests: 跨层测试
- E2E Tests: 完整流程测试（注册→登录→申请）

## 技术栈确认
- Java 17
- Maven
- Jackson (JSON处理)
- JUnit 5 (单元测试)
- LocalDateTime (时间处理，Jackson自动序列化)

## 注意事项
[待确认] - 密码加密：当前使用明文存储，生产环境应使用BCrypt或类似算法

## 运行测试
```bash
mvn test
```

## 生成覆盖率报告
```bash
mvn test jacoco:report
```