# 接下来的任务步骤

## 已完成 ✅

### 第1阶段 - 基础设施 (100%)
- [x] 数据模型设计 (User, Student, Job, Application)
- [x] 自定义异常类 (DataAccessException, ValidationException, BusinessException)
- [x] 工具类 (FileUtil, ValidationUtil, DateUtil)
- [x] DAO 接口定义 (UserRepository, StudentRepository, JobRepository, ApplicationRepository)
- [x] UserRepository 完整实现 + 18个单元测试
- [x] JSON 数据格式设计和示例
- [x] 架构文档和数据流图

## 待完成 ⏳

### 第2阶段 - DAO 实现 (待做)

#### 任务 2.1: JsonStudentRepository 实现 (预计 1-2 小时)
需要实现的方法：
```java
Student create(Student student)
Optional<Student> findById(String id)
Optional<Student> findByUserId(String userId)
Optional<Student> findByStudentId(String studentId)
List<Student> findAll()
Student update(Student student)
boolean delete(String id)
boolean existsByStudentId(String studentId)
boolean existsById(String id)
int count()
```

**关键点**：
- 参考 JsonUserRepository 的实现模式
- 使用 ValidationUtil 验证邮箱、电话号码
- 处理 skills 列表的序列化

**测试用例建议**：
- 创建学生
- 查询学生（按ID、userId、studentId）
- 更新学生信息和技能列表
- 删除学生
- 检查学号重复

#### 任务 2.2: JsonJobRepository 实现 (预计 1-2 小时)
需要实现：
```java
Job create(Job job)
Optional<Job> findById(String id)
List<Job> findAll()
List<Job> findByStatus(Job.JobStatus status)
List<Job> findByCourseCode(String courseCode)
Job update(Job job)
boolean delete(String id)
boolean existsById(String id)
int count()
```

**关键点**：
- 处理 Job.JobStatus 枚举
- 处理 requiredSkills 列表
- 流过滤操作示例：`users.stream().filter(...).toList()`

#### 任务 2.3: JsonApplicationRepository 实现 (预计 2-3 小时)
**复杂性最高** - 需要多条件查询和关联操作

需要实现：
```java
Application create(Application application)
Optional<Application> findById(String id)
List<Application> findByStudentId(String studentId)
List<Application> findByJobId(String jobId)
Optional<Application> findByJobIdAndStudentId(String jobId, String studentId)
List<Application> findAll()
List<Application> findByStatus(Application.ApplicationStatus status)
Application update(Application application)
boolean delete(String id)
boolean existsById(String id)
int count()
int countByJobId(String jobId)
```

**关键点**：
- 防止重复申请（同一学生不能申请同一岗位两次）
- 处理 Application.ApplicationStatus 枚举
- 处理可空字段（reviewedAt, reviewedBy, reviewComment）

#### 任务 2.4: 单元测试 (每个实现 ~15-20 个测试用例)
- [ ] JsonStudentRepositoryTest (~18 tests)
- [ ] JsonJobRepositoryTest (~15 tests)
- [ ] JsonApplicationRepositoryTest (~20 tests)

### 第3阶段 - 验证工具测试 (待做)

- [ ] ValidationUtilTest (15-20 tests)
  - 测试邮箱验证
  - 测试用户名验证
  - 测试密码强度
  - 测试电话验证
  - 测试长度验证

- [ ] FileUtilTest (10-15 tests)
  - 测试读写 JSON 数组
  - 测试读写 JSON 对象
  - 测试文件不存在时的行为
  - 测试目录创建
  - 测试临时文件失败情况

### 第4阶段 - Service 层实现 (待做)

实现业务逻辑层：
- [ ] UserService
  - registerUser()
  - authenticate()
  - updateProfile()
  - changePassword()

- [ ] StudentService
  - registerStudent()
  - updateStudentProfile()
  - uploadCV()
  - getStudentInfo()

- [ ] JobService
  - createJob()
  - listOpenJobs()
  - updateJobStatus()
  - getJobById()

- [ ] ApplicationService
  - submitApplication()
  - getApplications()
  - getApplicationsByStatus()
  - approveApplication()
  - rejectApplication()

### 第5阶段 - Controller 层实现 (待做)

实现 HTTP 控制器或事件监听器：
- [ ] AuthController (login/register)
- [ ] StudentController (profile management)
- [ ] JobController (job listing/details)
- [ ] ApplicationController (apply/review)

### 第6阶段 - 前端连接 (待做)

- [ ] 连接 JSP 页面与后端逻辑
- [ ] 完整的登录流程
- [ ] 完整的注册流程
- [ ] 完整的申请流程

## 优先级建议

### 本周 (Week 1)
1. **完成 JsonStudentRepository + 测试** (2-3 小时)
2. **完成 JsonJobRepository + 测试** (2-3 小时)
3. **完成 JsonApplicationRepository + 测试** (3-4 小时)

### 下周 (Week 2)
1. ValidationUtilTest 和 FileUtilTest (2-3 小时)
2. 代码审查和 bug 修复 (1-2 小时)
3. 开始 Service 层设计 (2-3 小时)

## 代码质量检查清单

在提交代码前，确保：
- [ ] 所有方法都有 JavaDoc 注释
- [ ] 异常有明确的错误消息
- [ ] 变量名清晰且遵循命名规范
- [ ] 没有魔法字符串（使用常量）
- [ ] 代码遵循单一职责原则
- [ ] 所有验证都在 validation 层
- [ ] 所有文件操作都通过 FileUtil
- [ ] 没有 null 指针异常（使用 Optional 或验证）
- [ ] 测试用例覆盖率 > 80%

## Git 提交规范

提交信息格式：
```
feat: 实现 JsonStudentRepository DAO 层

- 实现 10 个 CRUD 方法
- 添加数据验证和错误处理
- 包含 18 个单元测试
- 遵循 FileUtil 文件操作规范

Related: #45
```

## 测试运行命令

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=JsonUserRepositoryTest

# 运行特定测试方法
mvn test -Dtest=JsonUserRepositoryTest#testCreateUser

# 生成覆盖率报告
mvn clean test jacoco:report
```

## 常见问题 (FAQ)

Q: 为什么要使用临时文件方式写入？
A: 防止崩溃或中断导致的部分写入，保证原子性

Q: 为什么使用 Optional 而不是返回 null？
A: 明确表示可能没有结果，避免空指针异常

Q: 密码应该如何加密？
A: [待确认] - 目前使用明文，建议使用 BCrypt (bcrypt-maven 依赖)

Q: 如何处理并发访问？
A: [待确认] - 目前不考虑，后续可添加文件锁定或 synchronized

Q: 需要删除测试数据吗？
A: 可以在 @BeforeEach 中清空 data/users.json，或保留作为测试数据

## 相关文件位置

- 模型类: `src/main/java/edu/bupt/tarecruitment/model/`
- 工具类: `src/main/java/edu/bupt/tarecruitment/common/`
- 异常类: `src/main/java/edu/bupt/tarecruitment/common/exception/`
- DAO 接口: `src/main/java/edu/bupt/tarecruitment/persistence/repository/`
- DAO 实现: `src/main/java/edu/bupt/tarecruitment/persistence/json/`
- 测试: `src/test/java/edu/bupt/tarecruitment/`
- 数据文件: `data/`

## 持续整合 (CI)

推荐配置 GitHub Actions，在每次 push 时自动运行测试：

```yaml
# .github/workflows/test.yml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: mvn test
```

---

**总结**：您已经完成了 25% 的工作。接下来的重点是实现剩余三个 Repository 类及其测试。预计需要 8-12 小时完成整个 DAO 层。