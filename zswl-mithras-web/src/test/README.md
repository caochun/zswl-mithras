# zswl-mithras-web Test Scripts

`zswl-mithras-web/src/test` 现在分为两类：可维护测试和历史手工脚本。Maven 只编译可维护测试，历史手工脚本暂时隔离，避免模块边界重构被旧包名、外部环境和数据修复脚本阻塞。

## Maven 编译范围

`src/test/java/cn/zswltech/mithras/unittest/**` 是当前保留在 Maven `test-compile` 中的测试代码，共 11 个 Java 文件，主要覆盖 mapper、service 和少量报表处理逻辑。

这部分代码应该满足：

- 能在本地或 CI 的标准测试环境中编译。
- 不直接连接 pre、uat、prod 等外部环境。
- 不执行数据订正、批量导出、流程重放、外部接口调用等有副作用的动作。
- 包名、import 和当前主干代码保持同步。

## 被隔离的历史脚本

`src/test/java/cn/zswltech/mithras/others/**` 共 276 个 Java 文件，当前通过 `zswl-mithras-web/pom.xml` 的 `maven-compiler-plugin` `testExcludes` 排除。

顶层 `src/test/java/cn/zswltech/mithras/*Test.java` 共 3 个文件，也暂时排除：

- `AppServiceTest.java`
- `AppraisalCompanyWhitelistTest.java`
- `DataOpenClientTest.java`

这些文件不是稳定单元测试，更接近历史手工脚本、运维脚本、数据修复脚本、导出脚本、流程调试脚本或外部环境联调脚本。隔离的主要原因是：

- 大量文件依赖已经迁移或删除的旧包名，例如 `contract.core.application`、`contract.archive`、`third.service`、`monthly` 等。
- 多个脚本使用 `@ActiveProfiles("pre")`、`@ActiveProfiles("uat")`、`@ActiveProfiles("prod")`，存在误连外部环境或修改真实数据的风险。
- 很多脚本语义是“一次性动作”，不适合无条件进入 Maven 编译和 CI 流程。
- 部分脚本依赖本地文件、账号、远程接口、数据库状态或人工输入，无法作为可重复测试维护。

## 历史脚本分类

| 路径或模式 | 数量/范围 | 当前定位 |
| --- | ---: | --- |
| `others/service/**` | 107 | 历史服务联调、job 调试、跨域业务脚本 |
| `others/hand/**` | 42 | 手工导入、抽取、订正脚本 |
| `others/flow/**` | 29 | 流程启动、流程回放、审批调试脚本 |
| `others/数据导出/**` | 12 | 手工数据导出脚本 |
| `others/数据订正/**` | 10 | 手工数据订正脚本 |
| `others/render/**` | 10 | 文档/合同渲染手工验证 |
| `others/generator/**` | 8 | 代码或数据生成辅助工具 |
| `others/simple/**` | 7 | 临时验证、小工具 |
| `others/client/**` | 6 | 客户相关手工抽取/检查 |
| `others/bigbear/**` | 6 | 历史专项脚本 |
| `others/kpi/**` | 4 | KPI 手工验证 |
| 其他 `others/**` 根文件或小目录 | 45 | 一次性脚本、PoC、导出、OCR、邮件、PDF、报表等 |

## 后续处理原则

不要批量修复这些脚本的 import，也不要为了让它们通过编译而重新引入错误的模块依赖。每个脚本恢复前先判断它属于哪一种：

1. 可维护测试：移动或改造到 `unittest/**`，消除外部环境依赖，保证可重复运行，再纳入 Maven 编译。
2. 手工运维脚本：保留隔离状态，必要时后续迁到独立 `manual` 或 `ops` 目录，并补充运行说明。
3. 一次性历史脚本：确认无人使用后删除。
4. 业务能力样例：不要留在 web 测试目录，应转成对应业务域模块内的测试或文档示例。

恢复某个脚本进入 Maven 编译时，至少要完成：

- 移除 `pre`、`uat`、`prod` profile 或改为本地可控 profile。
- 修正旧包名，但不能因此新增不合理的业务域依赖。
- 确认不会修改真实数据或调用真实外部系统。
- 在当前模块下执行 `mvn -pl zswl-mithras-web -am -DskipTests test-compile` 通过。

## 当前结论

这批脚本先隔离、再按需复活是合适的。它们有历史价值，但不应该继续作为模块边界整理和主干编译的阻塞项。
