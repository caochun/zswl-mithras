# 模块宏观业务语义

`zswl-mithras-ftp` 是 FTP 定价域模块，负责维护老版与新版 FTP 定价指导、FTP 计息、FTP 收益维护，以及业务场景中的 FTP 实际取价能力。

本模块的核心语义是“资金转移定价规则与计算结果”，不是合同、付款、项目、客户或流程本身。项目、客户、合同、付款、资金等外部事实应尽量通过 port 或 application adapter 输入到 FTP 域，避免 FTP 直接承担其他业务域的数据组装职责。

oldftp 与 newftp 代表历史版本和新版定价体系，当前需要并存。后续整理应优先降低跨域依赖、拆薄取价服务，而不是删除 oldftp。

# zswl-mithras-ftp 模块梳理

`zswl-mithras-ftp` 承载 FTP 定价、FTP 计息、FTP 收益维护，以及新版 FTP 定价指导审批和取价能力。当前目录存在明显历史演进痕迹：`oldftp`、`newftp`、`flow`、根包 `convert` 并存，且部分“新版”代码仍复用老版流程模型、老版状态枚举和老版 BO。

## 模块职责

FTP 模块大致分成四类能力：

1. 老版 FTP 定价指导
   - 月度 FTP 定价指导：`oldftp/service/FtpMonthlyGuidanceService`
   - 季度最低收益率指导：`oldftp/service/FtpQuarterlyGuidanceService`
   - 对应定价明细、版本库、Excel 导入导出、流程提交和审批结束处理。

2. 新版 FTP 定价指导
   - 主表：`newftp/model/NewFtpBaseInfo`
   - 主服务：`newftp/service/NewFtpBaseInfoService`
   - 版本服务：`newftp/service/NewFtpVersionService`
   - 配置区：`newftp/model/config`、`newftp/service/config`、`newftp/controller/config`
   - 草稿区：`newftp/model/draft`、`newftp/service/draft`、`newftp/controller/draft`
   - 生效版本库：`newftp/model/lib`、`newftp/service/lib`、`newftp/lib/impl`

3. FTP 实际取价/计算
   - 新版取价入口主要在 `newftp/service/FtpService`。
   - `getBillFtp` 取买入价/卖出价。
   - `getCashFtp` 根据生效 FTP、期限、行业分类、资产分类、客户主体分类、地区分类、项目管理层级等条件取现金 FTP。
   - 仍保留 `getCashFtpDeprecated` 兼容旧数据或旧版本算法。

4. FTP 计息/收益维护
   - 计息基础与明细：`interest/model/FtpInterestBaseInfo`、`interest/model/FtpInterestDetailRecord`
   - 收益基础与明细：`income/model/FtpIncomeBaseInfo`、`income/model/FtpIncomeDetailRecord`
   - Job：`interest/job/FtpInterestJob`、`income/job/FtpIncomeJob`、`income/job/FtpIncomeRateInitJob`
   - 这部分已从 `oldftp` 拆出为独立子域。

## 当前目录语义

```text
cn.zswltech.mithras.ftp
├── common
│   ├── bo                                    # 老版/新版共同使用的取价、收益计算 BO
│   ├── convert                               # 跨 FTP/项目行业分类的通用转换
│   └── enums                                 # 老版/新版共同使用的流程状态、业务版本枚举
├── flow
│   ├── dynamicform/ftp                       # FTP 流程节点动态表单处理
│   └── listener/endhandler                   # 老版/新版 FTP 流程结束处理
├── interest                                  # FTP 计息：controller、job、model、mapper、service 接口
├── income                                    # FTP 收益：controller、job、model、mapper、service 接口
├── oldftp
│   ├── controller                            # 老版 FTP 指导 API 实现
│   ├── service                               # 老版指导服务、版本服务
│   ├── service/application                   # controller 使用的 application service 接口和实现
│   ├── mapper / mapper/lib                   # 主表 mapper 与版本库 mapper
│   ├── model                                 # 老版指导与版本库实体
│   ├── lib / lib/handler                     # 老版版本快照服务与 handler
│   ├── fms                                   # 老版状态机
│   ├── datacompare                           # 老版版本对比工厂
│   └── job                                   # XXL-JOB 入口
└── newftp
    ├── controller                            # 新版主入口、取价变更入口
    ├── controller/config                     # 新版配置区接口
    ├── controller/draft                      # 新版草稿区接口
    ├── service                               # 主服务、版本服务、实际取价服务
    ├── service/config                        # 配置区服务
    ├── service/draft                         # 草稿区服务
    ├── service/lib                           # 版本库查询服务
    ├── service/job                           # 新版自动计算 job 抽象
    ├── service/port                          # 文件模板 port
    ├── mapper / mapper/config/draft/lib      # mapper 按数据区拆分
    ├── model / model/config/draft/lib        # 实体按数据区拆分
    ├── lib / lib/impl                        # 版本快照 handler
    ├── fms                                   # 新版状态机
    ├── datacompare                           # 新版版本对比工厂
    ├── excel                                 # SHIBOR、国债收益率导入
    └── enums / utils                         # 新版枚举和计算工具
```

## 流程与版本

老版和新版 FTP 都围绕“创建审批”和“变更审批”运转。资源文件在：

```text
src/main/resources/bpmn/月度ftp定价指导创建审批流程.bpmn20.xml
src/main/resources/bpmn/月度ftp定价指导变更审批流程.bpmn20.xml
src/main/resources/bpmn/季度最低收益率指导创建审批流程.bpmn20.xml
src/main/resources/bpmn/季度最低收益率指导变更审批流程.bpmn20.xml
```

需要特别注意：

- 新版 FTP 的 `NewFtpBusinessModule.NEW_FTP_GUIDANCE` 仍绑定 `FtpMonthlyGuidanceCreateFlow` 与 `FtpMonthlyGuidanceModifyFlow`。
- 新版主服务 `NewFtpVersionService.submit` 根据 `ftpRecordStatus` 判断走创建流程还是修改流程，但流程 key 仍是月度 FTP 指导流程。
- `FtpBusinessVersion`、`FtpProcessStatus` 已放入 `common/enums`，因为它们不是纯老版概念。
- 老版使用 `FtpMonthlyGuidanceVersionService`、`FtpQuarterlyGuidanceVersionService` 和 `AbstractFtpMonthlyLibHandler` / `AbstractFtpQuarterlyLibHandler` 管理版本快照。
- 新版使用 `NewFtpVersionService` 和 `NewFtpLibAbstractHandler` 管理配置区/草稿区到 lib 区的版本快照。

流程扩展点：

- 动态表单：`flow/dynamicform/ftp`
  - `FtpChooseJudgesHandler`
  - `ShowFtpVotingResultsHandler`
  - `SetFtpMeetingFileHandler`
  - `SetFtpSupplementHandler`
- 流程结束：
  - `FtpQuarterlyGuidanceProcessEndHandler`
  - `NewFtpProcessEndHandler`

## 对外接口

老版相关 controller：

- `oldftp/controller/FtpMonthlyGuidanceController`
- `oldftp/controller/FtpQuarterlyGuidanceController`
- `oldftp/controller/FtpMaterialsFileController`
- `oldftp/controller/FtpInterestController`
- `oldftp/controller/FtpIncomeBaseInfoController`
- `oldftp/controller/FtpIncomeDetailRecordController`

新版相关 controller：

- `newftp/controller/NewFtpBaseInfoController`
- `newftp/controller/NewFtpInterestChangeController`
- `newftp/controller/config/*ConfigController`
- `newftp/controller/draft/*DraftController`

API 契约在 `zswl-mithras-api`：

- 老版：`cn.zswltech.mithras.api.ftp`
- 新版：`cn.zswltech.mithras.api.newftp`
- 新版配置：`cn.zswltech.mithras.api.newftp.config`

## 外部依赖关系

FTP 模块依赖较多业务域，主要原因是实际取价需要结合项目、客户、合同、付款、资金、风控和基础数据。

直接依赖的业务模块包括：

- `basedata`：LPR、日期等基础数据。
- `projectprocess`：项目定价、项目评审、行业/地区/项目管理分类。
- `customer`：工商信息、客户主体分类、地址、关联方判断。
- `contract`：合同、承租人、保证人、还款计划、租赁业务信息。
- `payment`：付款相关 FTP 评估信息。
- `fund`：融资担保、直接融资质押等资金信息。
- `riskcontrol`：风控行业分类。
- `workflow`：审批流程、动态表单、流程结束。
- `system`：用户、部门、权限和名称转换。
- `document`：材料、文件模板和会议纪要等文件能力。

`zswl-mithras-application` 也会反向承接或引用 FTP：

- `application/orchestration/ftp` 里有 FTP 收益和计息聚合服务。
- `application/orchestration/enums/BusinessModuleEnum` 绑定老版和新版 FTP 的业务模块、mapper、流程类型。
- `application/orchestration/workflow/datacompare/enums/CompareFactoryEnum` 注册 FTP 版本对比项。
- `application/orchestration/document/file/impl/FtpQuarterlyFileListProvider` 提供季度 FTP 材料列表。
- 项目定价、收益率报告渲染等场景会调用 `FtpService` 或 `CommonConvert`。

## 当前不规范点

1. `oldftp` 名称误导
   - 原先目录里既有老版月度/季度指导，也有仍在使用的计息、收益服务。
   - 原先被新版复用的 BO、通用状态枚举和业务版本枚举已迁到 `common`，计息/收益已分别迁到 `interest`、`income`。

2. `newftp` 内部按技术层和数据状态混排
   - `config`、`draft`、`lib` 同时出现在 controller/service/mapper/model 下。
   - 这是“配置区/草稿区/版本区”的业务概念，但目录上和常规 controller/service/mapper 分层交织，阅读成本高。

3. 流程归属不清
   - `flow/dynamicform/ftp` 同时服务新版和老版部分流程。
   - 新版流程模型仍叫 `FtpMonthlyGuidanceCreateFlow` / `FtpMonthlyGuidanceModifyFlow`，语义偏旧。

4. `FtpService` 职责过重
   - 它同时处理实际取价、客户/合同/项目上下文组装、旧算法兼容、担保/承租人/地区分类判断。
   - 这个类是当前最核心但也最容易继续膨胀的服务。

5. 应用层边界不稳定
   - `zswl-mithras-ftp` 直接依赖多个业务域。
   - `zswl-mithras-application` 又有 `orchestration/ftp`，导致 FTP 业务有一部分在模块内，一部分在编排层。

6. 命名不统一
   - `FtpProcessStatus` 与 `NewFtpProcessStatus` 并存。
   - `monthlyGuidanceExt`、`quarterlyBasePricingExt` 等 `Ext` 表达不清楚。
   - `crditTerm` 等字段存在历史拼写问题，短期不宜直接改字段，但文档和新代码应避免继续扩散。

## 建议目标结构

如果后续要整理 package，建议不要一次性大搬迁。先明确新目标结构，再分阶段迁移。

建议目标：

```text
cn.zswltech.mithras.ftp
├── common
│   ├── bo
│   ├── convert
│   ├── enums
│   └── fms
├── pricing
│   ├── legacy                         # 老版月度/季度指导，仅保留旧指导相关
│   │   ├── monthly
│   │   └── quarterly
│   └── guidance                       # 新版 FTP 指导
│       ├── base                       # 主表/主服务/主 controller
│       ├── config                     # 配置区
│       ├── draft                      # 草稿区
│       ├── version                    # lib、handler、version service
│       ├── compare
│       ├── calculate
│       └── fms
├── interest                           # FTP 计息
├── income                             # FTP 收益维护
├── flow                               # FTP 相关流程适配
├── job                                # FTP 定时任务入口
└── integration                        # port/adapter 或跨域调用封装
```

短期更现实的整理顺序：

1. 已加模块文档，明确 `oldftp` 不是废弃代码。
2. 已把 `oldftp.bo`、`oldftp.enums.FtpBusinessVersion`、`oldftp.enums.FtpProcessStatus`、根包 `convert/CommonConvert` 迁到 `ftp.common`。
3. 已把计息/收益从 `oldftp` 拆出到 `interest`、`income`，因为它们不是老版指导的一部分。
4. 下一步把 `newftp/service/FtpService` 拆成：
   - `FtpEffectiveGuidanceQueryService`：查询生效 FTP。当前已拆出。
   - `CashFtpPricingService`：现金 FTP 取价。
   - `BillFtpPricingService`：票据 FTP 买入/卖出价。当前已拆出。
   - `FtpPricingContextAssembler`：组装客户、项目、合同上下文。当前已拆出。
   - `LegacyFtpPricingSupport`：旧算法兼容。
5. 最后再考虑重命名 `newftp` 为 `pricing/guidance` 或类似目录。这个步骤影响 import 很多，应该最后做。

## 维护注意事项

- `interest`、`income` 的应用层实现仍主要在 `zswl-mithras-application/orchestration/ftp`，FTP 模块内保留 controller、mapper、model、job 入口和 service 接口。
- 修改新版审批时，要同时检查 `NewFtpVersionService`、`NewFtpBusinessModule`、BPMN、`BusinessModuleEnum` 和流程结束 handler。
- 修改取价逻辑时，要重点看 `newftp/service/FtpService#getCashFtp`、`getCashFtpDeprecated` 和项目定价/合同渲染调用点。
- 修改配置/草稿/版本区字段时，要同步改 `model/config`、`model/draft`、`model/lib`、mapper、converter、lib handler、datacompare factory。
- 涉及材料校验时，要注意 `SetFtpMeetingFileHandler`、`SetFtpSupplementHandler` 和 document/materials 侧的业务类型、材料类型是否一致。
