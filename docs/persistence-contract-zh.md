# 持久化层开发说明

## 1. 当前已完成内容

目前已经完成并通过最小测试的持久化链路有三条：

### 1.1 Student 链路

- `src/main/java/edu/bupt/tarecruitment/model/Student.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/StudentRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonStudentRepository.java`
- `data/students.json`
- `src/test/java/edu/bupt/tarecruitment/persistence/json/JsonStudentRepositoryTest.java`

### 1.2 Job 链路

- `src/main/java/edu/bupt/tarecruitment/model/Job.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/JobRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonJobRepository.java`
- `data/jobs.json`
- `src/test/java/edu/bupt/tarecruitment/persistence/json/JsonJobRepositoryTest.java`

### 1.3 Application 链路

- `src/main/java/edu/bupt/tarecruitment/model/Application.java`
- `src/main/java/edu/bupt/tarecruitment/model/ApplicationStatus.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/ApplicationRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonApplicationRepository.java`
- `data/applications.json`
- `src/test/java/edu/bupt/tarecruitment/persistence/json/JsonApplicationRepositoryTest.java`

### 1.4 公共持久化基础

- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonDataStore.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/AbstractJsonRepository.java`
- `src/main/java/edu/bupt/tarecruitment/common/exception/DataAccessException.java`
- `src/main/java/edu/bupt/tarecruitment/common/exception/JsonFormatException.java`

## 2. 当前统一约定

### 2.1 项目与分层

- 当前项目是 `Java 17 + Swing + JSON` 的本地桌面应用。
- 不是 Servlet Web 应用。
- 持久化分层固定为：`model -> persistence.repository -> persistence.json -> data/*.json`
- JSON 是唯一持久化真源。
- 不引入数据库。
- 不引入 Spring Boot 或其他重框架。

### 2.2 三类实体的最小模型

#### Student

- 最小字段集：`id`、`name`、`userId`
- `id`：学生编号/学号，同时作为持久化主键
- `userId`：引用 `User.id`

#### Job

- 最小字段集：`id`、`title`、`description`
- `id`：岗位编号，同时作为持久化主键

#### Application

- 最小字段集：`id`、`studentId`、`jobId`、`status`
- `id`：系统内部申请记录编号，同时作为持久化主键
- `studentId`：引用 `Student.id`
- `jobId`：引用 `Job.id`
- `studentId` 和 `jobId` 只是业务关联字段，不是组合主键

### 2.3 ApplicationStatus 最小状态集

当前已固定的最小状态集为：

- `SUBMITTED`
- `APPROVED`
- `REJECTED`

### 2.4 Repository 接口约定

三类实体当前统一采用以下最小 CRUD 接口：

- `findAll()`
- `findById(String id)`
- `insert(T entity)`
- `update(T entity)`
- `deleteById(String id)`

当前约定：

- Repository 接口命名先保持不变
- Repository 返回领域对象、列表、`Optional<T>` 或 `boolean`
- Repository 不返回 UI 文本
- Repository 不返回原始 JSON 字符串

### 2.5 异常语义

统一使用显式异常：

- `DataAccessException`
- `JsonFormatException`

当前固定语义：

- `insert` 时若主键重复，抛 `DataAccessException`
- `update` 时若目标 `id` 不存在，统一抛 `DataAccessException`
- JSON 格式非法时，抛 `JsonFormatException`
- 必需 JSON 文件不存在或不可读时，抛 `DataAccessException`

### 2.6 JSON 文件约定

当前数据文件：

- `data/students.json`
- `data/jobs.json`
- `data/applications.json`
- `data/users.json`

统一约定：

- JSON 根结构统一为数组
- JSON 字段顺序不作为强契约
- JSON 非法必须清晰失败，不能静默吞掉错误

示例结构：

```json
[
  {
    "id": "S001",
    "name": "Alice",
    "userId": "U001"
  }
]
```

```json
[
  {
    "id": "J001",
    "title": "Java TA",
    "description": "Assist with labs"
  }
]
```

```json
[
  {
    "id": "A001",
    "studentId": "S001",
    "jobId": "J001",
    "status": "SUBMITTED"
  }
]
```

## 3. 当前 [待确认] 内容

### 3.1 Student

- 是否在持久化层强制 `userId` 唯一
- 是否补充专业、年级、联系方式等扩展字段

### 3.2 Job

- `Job.id` 是否还有更严格的业务格式要求
- 是否补充课程、教师、名额、截止时间、发布人、岗位状态等字段
- 是否增加按课程、教师、状态等查询接口

### 3.3 Application

- 完整状态流转规则
- 是否补充 `submittedAt`、`reviewedAt`、`comment`、`reviewerId` 等字段
- 是否做 `studentId`、`jobId` 的引用存在性校验
- 是否增加按 `studentId`、`jobId`、`status` 查询的接口

### 3.4 业务规则归属

- “同一 `studentId` 对同一 `jobId` 默认不允许重复申请” 这条规则已经确认存在
- 但当前明确不写进 DAO / Repository
- 后续优先由 service 层处理

### 3.5 User

- `User` 模型最终字段
- 认证相关语义
- 与其它实体的最终关联方式

## 4. 给后续 service / UI 同学的接入约定

### 4.1 service 层

service 层应：

- 依赖 `persistence.repository` 接口，不直接依赖 JSON 实现细节
- 在调用持久化前处理业务规则
- 捕获并处理 `DataAccessException`、`JsonFormatException`

优先放在 service 层处理的规则：

- 同一学生不可重复申请同一岗位
- `studentId` / `jobId` 是否存在
- Application 状态流转是否合法

### 4.2 UI 层

UI 层应：

- 只调用 controller / service
- 不直接读写 JSON 文件
- 不直接依赖 `persistence.json` 实现类
- 不在界面层处理底层文件读写逻辑

## 5. 后续扩展时应该修改哪些文件

原则：如果字段、状态、结构或存储语义发生变化，必须整套同步修改，不要只改一个文件。

### 5.1 扩展 Student 时

需要同步修改：

- `src/main/java/edu/bupt/tarecruitment/model/Student.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/StudentRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonStudentRepository.java`
- `data/students.json`
- `src/test/java/edu/bupt/tarecruitment/persistence/json/JsonStudentRepositoryTest.java`
- 本文档

### 5.2 扩展 Job 时

需要同步修改：

- `src/main/java/edu/bupt/tarecruitment/model/Job.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/JobRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonJobRepository.java`
- `data/jobs.json`
- `src/test/java/edu/bupt/tarecruitment/persistence/json/JsonJobRepositoryTest.java`
- 本文档

### 5.3 扩展 Application 时

需要同步修改：

- `src/main/java/edu/bupt/tarecruitment/model/Application.java`
- `src/main/java/edu/bupt/tarecruitment/model/ApplicationStatus.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/repository/ApplicationRepository.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonApplicationRepository.java`
- `data/applications.json`
- `src/test/java/edu/bupt/tarecruitment/persistence/json/JsonApplicationRepositoryTest.java`
- 本文档

### 5.4 扩展通用持久化行为时

需要同步修改：

- `src/main/java/edu/bupt/tarecruitment/persistence/json/JsonDataStore.java`
- `src/main/java/edu/bupt/tarecruitment/persistence/json/AbstractJsonRepository.java`
- `src/main/java/edu/bupt/tarecruitment/common/exception/DataAccessException.java`
- `src/main/java/edu/bupt/tarecruitment/common/exception/JsonFormatException.java`
- 相关测试
- 本文档

## 6. 最终说明

当前 persistence 层的目标不是一次性做完所有业务，而是先稳定最小可运行闭环。

开发优先级应遵守：

1. 先遵守课程约束
2. 再遵守三层分层边界
3. 再在已确认范围内扩展功能

凡是课程文档、组内约定、现有代码、测试没有明确确认的内容，都应继续标注为 `[待确认]`。