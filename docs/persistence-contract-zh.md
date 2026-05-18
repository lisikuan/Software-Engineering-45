# 持久化契约说明

## 1. 范围

本文档描述 TA Recruitment System 当前的持久化层契约。

架构边界：

- `model -> persistence.repository -> persistence.json -> data/*.json`

约束：

- JSON 是唯一持久化数据源
- 不引入数据库
- 不引入 Spring Boot 或其他重型框架
- 持久化层只负责存储机制，不负责完整业务规则

## 2. 当前最小模型

### 2.1 Student

当前字段：

- `id`
- `name`
- `userId`
- `studentNumber`
- `skillTags`
- `cvFilePath`

当前含义：

- `id`：学生档案记录主键
- `userId`：关联 `User.id`
- `studentNumber`：学生学号，供个人档案使用
- `skillTags`：技能标签，使用 JSON 数组存储
- `cvFilePath`：CV 文件相对路径，固定落在 `data/cvs/` 下

[待确认]：

- 是否需要在持久化层强制 `userId` 唯一
- 是否补充专业、年级、联系方式等字段

### 2.2 Job

当前字段：

- `id`
- `title`
- `description`
- `courseName`
- `requiredSkills`
- `weeklyHours`

当前含义：

- `id`：岗位主键，当前由 service 层自动生成
- `title`：当前 Swing 列表展示标题
- `courseName`：管理员发布岗位时录入的课程名
- `requiredSkills`：岗位要求技能，使用 JSON 数组存储
- `weeklyHours`：每周工时

[待确认]：

- `Job.id` 是否还需要更严格的业务格式
- 是否补充教师、名额、截止时间、发布人、岗位状态等字段

### 2.3 Application

当前字段：

- `id`
- `studentId`
- `jobId`
- `status`

当前状态集：

- `SUBMITTED`
- `APPROVED`
- `REJECTED`

当前含义：

- `id`：申请记录主键
- `studentId`：关联 `Student.id`
- `jobId`：关联 `Job.id`

[待确认]：

- 更完整的状态流转规则
- 是否补充提交时间、审核时间、审核人、备注等字段

### 2.4 User

当前字段：

- `id`
- `username`
- `password`
- `role`

当前含义：

- `id`：用户主键
- `username`：登录用户名
- `password`：测试版明文密码，仅用于本地演示
- `role`：当前使用 `STUDENT`、`ADMIN`

[待确认]：

- 是否需要拆分 `MO` 角色
- 更安全的密码存储方式

## 3. JSON 文件

当前持久化文件：

- `data/students.json`
- `data/jobs.json`
- `data/applications.json`
- `data/users.json`
- `data/cvs/*.pdf`

统一约定：

- JSON 根结构统一为数组
- 字段顺序不作为强约束
- 非法 JSON 必须清晰失败

示例：

```json
[
  {
    "id": "S001",
    "name": "Alice",
    "userId": "U001",
    "studentNumber": "2024001",
    "skillTags": ["Java", "Communication"],
    "cvFilePath": "cvs/S001.pdf"
  }
]
```

```json
[
  {
    "id": "J001",
    "title": "Java Programming TA",
    "description": "Assist with labs",
    "courseName": "Java Programming",
    "requiredSkills": ["Java", "Communication"],
    "weeklyHours": 6
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

## 4. Repository 契约

当前 `Student`、`Job`、`Application`、`User` 统一采用：

- `findAll()`
- `findById(String id)`
- `insert(T entity)`
- `update(T entity)`
- `deleteById(String id)`

统一语义：

- 返回领域对象、列表、`Optional<T>` 或 `boolean`
- 不返回 UI 文本
- 不返回原始 JSON 字符串
- 主键重复插入必须显式失败
- 更新不存在的 `id` 必须抛 `DataAccessException`
- CV 文件存储通过单独持久化入口处理，不把文件内容直接写进 JSON

## 5. 异常约定

持久化层使用显式异常：

- `DataAccessException`
- `JsonFormatException`

当前语义：

- `DataAccessException`：文件访问失败、主键冲突、更新目标不存在等
- `JsonFormatException`：JSON 结构非法或映射失败

## 6. Service / UI 集成约定

### 6.1 Service 层

Service 层应：

- 依赖 repository 接口，而不是 JSON 实现细节
- 在调用持久化前处理业务规则
- 处理 `DataAccessException` 和 `JsonFormatException`

当前放在 service 层处理的规则：

- 同一学生不能重复申请同一岗位
- 引用的学生和岗位必须存在
- 申请状态流转必须合法
- 登录凭证必须和 `users.json` 一致
- 学号必须唯一
- 学生未完成个人档案前不能申请岗位

### 6.2 UI 层

UI 层应：

- 只调用 controller / service
- 不直接读写 JSON 文件
- 不直接依赖 `persistence.json` 实现

## 7. 当前 [待确认] 汇总

- Student、Job、Application 是否继续扩展字段
- `MO` 是否需要与 `ADMIN` 分离
- 更安全的密码存储策略
- Application 更完整的状态流转规则
- repository 是否需要增加更多按业务字段查询的方法
