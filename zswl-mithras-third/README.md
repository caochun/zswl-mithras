# zswl-mithras-third

`zswl-mithras-third` 是第三方集成模块，负责财务共享、苍穹/数据共享、企查查、天眼查、阿里 OCR、中登、风险舆情、逾期报送、云湖、数据矿工等外部系统对接。

本模块的核心语义是“外部系统访问、数据转换、回调落库和异常重试”。它不应承载调用方业务域的完整业务规则。

后续整理重点是把第三方通道能力与业务域编排隔离：third 提供外部能力，application 或业务域决定何时调用、如何使用结果。

## 资源归属

当前 `src/main/resources/sql` 中的脚本虽然有一些历史目录名带有“风控应用”“租前息”等业务字样，但从 Java 模型和服务归属看，大部分仍属于第三方防腐层自身：

- `data_share_*` 表对应 `third/datashare` 下的数据共享进度、客商、编码映射和附件同步能力。
- `exception_request_info`、`external_exception_info` 是第三方接口异常重试和调用失败记录。
- `sync_cq_record`、`cq_related_mithras` 是苍穹/财务共享请求记录和外部单据关联记录。
- `bill_overdue`、`public_outer_info_record`、`peer_comparison_*` 位于 `third/providence` 外部数据/公共信息查询/同业分析能力下；目录名叫“风控应用”不代表表归属 riskcontrol。

需要持续关注的是 `finance_flow_*`：这些持久化模型当前在 `third/financialshare`，但业务使用重心在资金流水和核销编排。短期不直接迁移，因为它们还和财务共享 API、临时流水、匹配结果、宝融同步、异常重试混在一起；长期应由 `capital` 定义自己的流水快照或流水事实模型，`third` 只保留外部财资平台同步和原始防腐能力。

## 合并判断

`third` 不应作为合并候选。它的价值是把外部系统协议、认证、请求重试、原始返回结构和落库记录隔离在业务域之外。后续收敛方向不是合并 `third`，而是减少业务域直接使用 `third` 的持久化模型和 client DTO。

依赖整理记录：模块内 `XinsightMySqlConfig` 使用 `org.mybatis.spring.annotation.MapperScan`、MyBatis core 的 `Interceptor`、`SqlSessionFactory`、`JdbcType` 等类型，以及 MyBatis-Plus 的 `MybatisSqlSessionFactoryBean`；这些已由 `mybatis-plus-boot-starter` 依赖链覆盖，POM 中不再保留单独的 `mybatis-spring` 或 `org.mybatis:mybatis` 直接声明。
