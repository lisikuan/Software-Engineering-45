# 行动清单 - 立即执行

## 🎯 现在就做

### [ ] 1. 验证环境
- [ ] Java 17 已安装: `java -version`
- [ ] Maven 已安装: `mvn -version`
- [ ] IDE 已配置 (IntelliJ/Eclipse/VS Code)
- [ ] Git 已配置: `git config --list`

### [ ] 2. 检出代码
```bash
# 假设已添加到 GitHub
git clone https://github.com/lisikuan/Software-Engineering-45.git
cd Software-Engineering-45

# 或更新现有代码
git pull origin Lizhuolun/logic-integration
```

### [ ] 3. 编译项目
```bash
mvn clean compile
```

### [ ] 4. 运行测试
```bash
mvn test
```

**预期结果**: 18/18 tests passed ✅

### [ ] 5. 检查代码结构
```bash
# Linux/Mac
find src -name "*.java" -type f | wc -l

# 应该看到 ~22 个 Java 文件
```

## 📝 今天完成

### 今天 (第一天)
- [ ] 理解 AGENTS.md 规范 (30 分钟)
- [ ] 阅读 DATA_FLOW_DIAGRAM.md (30 分钟)
- [ ] 运行现有测试并验证通过 (20 分钟)
- [ ] 查看 JsonUserRepository 实现代码 (45 分钟)

### 明天 (第二天)
- [ ] 设计 JsonStudentRepository 接口
- [ ] 开始实现 JsonStudentRepository
- [ ] 编写 StudentRepository 测试用例

## 📅 本周计划

| 日期 | 任务 | 预计时间 | 优先级 |
|------|------|---------|--------|
| 周一 | 理解现有代码和架构 | 2 小时 | 🔴 高 |
| 周二 | 实现 JsonStudentRepository + 测试 | 3 小时 | 🔴 高 |
| 周三 | 实现 JsonJobRepository + 测试 | 3 小时 | 🔴 高 |
| 周四 | 实现 JsonApplicationRepository + 测试 | 4 小时 | 🔴 高 |
| 周五 | 工具类测试 + 代码审查 | 2-3 小时 | 🟡 中 |

## 🔍 代码审查清单

提交前检查:

- [ ] 所有类都有 JavaDoc
- [ ] 所有方法都有 JavaDoc
- [ ] 异常消息清晰明确
- [ ] 变量名有意义且遵循规范
- [ ] 没有魔法数字/字符串
- [ ] 没有 System.out.println (使用 logger)
- [ ] 没有 TODO 注释（或已解决）
- [ ] 测试覆盖率 > 80%
- [ ] 所有测试通过
- [ ] 遵循单一职责原则

## 📊 质量指标

### 代码质量目标
- 代码覆盖率: > 80%
- 测试通过率: 100%
- 代码重复率: < 10%
- 圈复杂度: < 10

### 文档目标
- 所有 public 方法都有注释
- 所有类都有类级注释
- 所有异常都有文档
- 数据流清晰可见

## 🐛 测试边界情况

确保考虑:
- [ ] 空值和 null 处理
- [ ] 边界值 (空字符串、特殊字符)
- [ ] 重复数据处理
- [ ] 文件不存在情况
- [ ] 权限问题
- [ ] 并发访问 [待确认]

## 🔐 安全检查

- [ ] 没有硬编码密码
- [ ] 没有敏感信息在日志中
- [ ] 输入验证完整
- [ ] SQL 注入风险: N/A (无数据库)
- [ ] 文件路径遍历检查: [待确认]

## 📈 性能检查

- [ ] 没有 N+1 查询问题
- [ ] 没有不必要的文件 I/O
- [ ] 没有大量内存分配
- [ ] 没有无限循环
- [ ] 响应时间 < 1 秒

## 🔄 Git 工作流

### 创建功能分支
```bash
git checkout Lizhuolun/logic-integration
```

### 进行开发
```bash
# 编辑文件
# 编写测试
# 运行测试
mvn test
```

### 提交代码
```bash
git add .
git commit -m "feat: 实现 JsonStudentRepository

- 实现 10 个 CRUD 方法
- 验证学号唯一性
- 处理技能列表序列化
- 包含 18 个单元测试

Related: #45"
```

### 推送到远程
```bash
git push origin Lizhuolun/logic-integration
```

### 创建 Pull Request
- 在 GitHub 上创建 PR
- 描述变更内容
- 等待代码审查
- 合并到主分支

## 🆘 遇到问题

### 编译错误
```bash
# 清理并重新编译
mvn clean compile

# 检查依赖
mvn dependency:tree
```

### 测试失败
```bash
# 运行单个测试获取详细输出
mvn test -Dtest=JsonUserRepositoryTest -X

# 清空数据文件重试
rm -rf data/*.json
mvn test
```

### JSON 序列化问题
- 检查 pom.xml 中的 Jackson 依赖
- 确保注册了 JavaTimeModule
- 检查 LocalDateTime 格式

### 文件权限问题
```bash
# Linux/Mac
chmod 755 data/

# Windows
# 右键 → 属性 → 安全 → 编辑权限
```

## 📞 需要帮助?

1. 查看 AGENTS.md - 架构规范
2. 查看 QUICK_REFERENCE.md - 快速参考
3. 查看 IMPLEMENTATION_GUIDE.md - 实现指南
4. 查看 JsonUserRepositoryTest - 测试示例
5. 查看异常堆栈跟踪 - 准确的错误定位

## ✅ 完成标记

- [ ] 所有 18 个测试通过
- [ ] 代码审查完成
- [ ] 文档已更新
- [ ] Git 提交已推送
- [ ] 没有 IDE 警告
- [ ] 性能达到要求

## 🎯 最终检查清单

提交前最后检查:

```
☐ 代码能编译
☐ 所有测试通过
☐ 没有警告信息
☐ 覆盖率 > 80%
☐ 遵守编码规范
☐ 文档完整
☐ Git 历史清晰
☐ 无 TODO 注释
☐ 异常处理完善
☐ 已删除调试代码
```

---

**开始行动!** 🚀

现在就运行:
```bash
mvn clean test
```

成功的标志是看到:
```
BUILD SUCCESS
Tests run: 18, Failures: 0, Errors: 0
```