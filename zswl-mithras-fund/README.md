# zswl-mithras-fund

`zswl-mithras-fund` 是资金融资域模块，负责资金融资、融资机构、融资收付、融资还款计划、授信担保、直接融资和财务系统提交。

本模块的核心语义是“公司资金来源、融资安排和融资还款”。它和合同、付款、收款、财务、流动性存在业务协作，但应保持融资域事实独立。

后续整理重点是明确 fund 与 capital、finance、liquidity 的边界：fund 管融资业务，capital 管流水核销，finance 管财务口径，liquidity 管流动性风险。
