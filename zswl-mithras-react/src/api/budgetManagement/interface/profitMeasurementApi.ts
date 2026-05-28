/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [预算管理-预算计划-利润预算-信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/34177) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/info`
 * @更新时间 `2025-05-22 16:52:33`
 */
export interface ProfitInfoRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/34177) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/info`
 * @更新时间 `2025-05-22 16:52:33`
 */
export interface ProfitInfoResponse {
  /**
   * 主键id
   */
  id?: number
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 计划名称
   */
  budgetPlanName?: string
  /**
   * 预算计划开始日期
   */
  budgetDateFrom?: string
  /**
   * 预算计划结束日期
   */
  budgetDateTo?: string
  /**
   * 计划填报开始日期
   */
  writeDateFrom?: string
  /**
   * 计划填报结束日期
   */
  writeDateTo?: string
  /**
   * 预算类型
   */
  budgetType?: string
  /**
   * 状态
   */
  budgetStatus?: string
  /**
   * 是否收集，0-否，1-是
   */
  needCollect?: number
  /**
   * 收集截止日期
   */
  collectDateTo?: string
  /**
   * 投放计划待办是否发送
   */
  isCollectTaskNotify?: number
  /**
   * 对应的投放计划id
   */
  budgetPlanPayId?: number
}

/**
 * 接口 [预算管理-预算计划-利润预算-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32371) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/pageList`
 * @更新时间 `2025-05-14 19:43:20`
 */
export interface ProfitPageListRequest {
  /**
   * 分页，默认1
   */
  page?: number
  /**
   * 页大小， 默认20
   */
  pageSize?: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32371) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/pageList`
 * @更新时间 `2025-05-14 19:43:20`
 */
export interface ProfitPageListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 预算计划id
     */
    budgetPlanId?: number
    /**
     * 计划名称
     */
    budgetPlanName?: string
    /**
     * 预算计划开始日期
     */
    budgetDateFrom?: string
    /**
     * 预算计划结束日期
     */
    budgetDateTo?: string
    /**
     * 计划填报开始日期
     */
    writeDateFrom?: string
    /**
     * 计划填报结束日期
     */
    writeDateTo?: string
    /**
     * 预算类型
     */
    budgetType?: string
    /**
     * 状态
     */
    budgetStatus?: string
    /**
     * 是否收集，0-否，1-是
     */
    needCollect?: number
    /**
     * 收集截止日期
     */
    collectDateTo?: string
    /**
     * 投放计划待办是否发送
     */
    isCollectTaskNotify?: number
  }[]
  /**
   * 数据总记录数。
   */
  total?: number
  /**
   * 总页数
   */
  pages?: number
  /**
   * 页大小
   */
  pageSize?: number
  /**
   * 当前页
   */
  currentPage?: number
  /**
   * 其他携带参数
   */
  others?: {}
}

/**
 * 接口 [预算管理-预算计划-利润预算-创建预算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32365) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/create`
 * @更新时间 `2025-05-20 09:19:11`
 */
export interface ProfitCreateRequest {
  /**
   * 预算区间开始日期
   */
  budgetDateFrom: string
  /**
   * 预算区间结束日期
   */
  budgetDateTo: string
  /**
   * 填报区间开始日期
   */
  writeDateFrom: string
  /**
   * 填报区间结束日期
   */
  writeDateTo: string
  /**
   * 预算类型
   */
  budgetType: string
  /**
   * 是否收集
   */
  needCollect: number
  /**
   * 收集截止日期
   */
  collectDateTo?: string
  /**
   * 原计划id（月度调整时使用）
   */
  originBudgetPlanId?: number
}

/**
 * 接口 [预算管理-预算计划-利润预算-创建预算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32365) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/create`
 * @更新时间 `2025-05-20 09:19:11`
 */
export type ProfitCreateResponse = number

/**
 * 接口 [预算管理-预算计划-利润预算-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/32377) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/remove`
 * @更新时间 `2025-04-14 10:08:56`
 */
export interface ProfitRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [预算管理-预算计划-利润预算-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/32377) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/remove`
 * @更新时间 `2025-04-14 10:08:56`
 */
export interface ProfitRemoveResponse {
  /**
   * 是否成功
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 提示信息
   */
  msg?: string
  /**
   * 异常时返回的异常信息
   */
  description?: string
  /**
   * 不阻断操作流程的toast提示
   */
  toast?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-发送待办（启动投放计划收集流程）↗](http://yapi.zswltec.com:3000/project/11/interface/api/34033) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/notifyCreatePlanPay`
 * @更新时间 `2025-05-18 17:33:05`
 */
export interface DetailNotifyCreatePlanPayRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-发送待办（启动投放计划收集流程）↗](http://yapi.zswltec.com:3000/project/11/interface/api/34033) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/notifyCreatePlanPay`
 * @更新时间 `2025-05-18 17:33:05`
 */
export interface DetailNotifyCreatePlanPayResponse {
  /**
   * 是否成功
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 提示信息
   */
  msg?: string
  /**
   * 异常时返回的异常信息
   */
  description?: string
  /**
   * 不阻断操作流程的toast提示
   */
  toast?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-投放进度↗](http://yapi.zswltec.com:3000/project/11/interface/api/33847) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/process`
 * @更新时间 `2025-05-18 16:24:39`
 */
export interface ProfitProcessRequest {
  /**
   * 利润预算id
   */
  budgetPlanProfitId: number
  /**
   * 部门id
   */
  belongDeptId?: number
  /**
   * 日期-起
   */
  queryDateFrom?: string
  /**
   * 日期-止
   */
  queryDateTo?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-投放进度↗](http://yapi.zswltec.com:3000/project/11/interface/api/33847) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/process`
 * @更新时间 `2025-05-18 16:24:39`
 */
export type ProfitProcessResponse = {
  /**
   * 日期
   */
  date?: string
  /**
   * 部门数据
   */
  deptDataList?: {
    /**
     * 部门id
     */
    deptId?: number
    /**
     * 部门名称
     */
    deptName?: string
    /**
     * 公用事业
     */
    publicUtilities?: number
    /**
     * 民生消费
     */
    civilConsumption?: number
    /**
     * 国有产业
     */
    stateOwnedIndustry?: number
    /**
     * 其他产业
     */
    otherIndustry?: number
  }[]
}[]

/**
 * 接口 [预算管理-预算计划-利润预算-汇总↗](http://yapi.zswltec.com:3000/project/11/interface/api/33835) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/summary`
 * @更新时间 `2025-05-21 16:24:04`
 */
export interface ProfitSummaryRequest {
  /**
   * 利润预算id
   */
  budgetPlanProfitId: number
  /**
   * 部门id
   */
  belongDeptId?: number
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-汇总↗](http://yapi.zswltec.com:3000/project/11/interface/api/33835) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/summary`
 * @更新时间 `2025-05-21 16:24:04`
 */
export interface ProfitSummaryResponse {
  /**
   * 部门数据
   */
  deptDataList?: {
    /**
     * 部门id
     */
    belongDeptId?: number
    /**
     * 部门名称
     */
    belongDeptName?: string
    /**
     * 数据列表
     */
    dataList?: {
      isSumRow?: boolean
      /**
       * FTP行业分类
       */
      ftpIndustryCategory?: string
      /**
       * FTP行业分类-展示文案
       */
      ftpIndustryCategoryDisplay?: string
      /**
       * 收入-存量
       */
      incomeHistory?: number
      /**
       * 成本-存量
       */
      costHistory?: number
      /**
       * 差价-存量
       */
      diffHistory?: number
      /**
       * 税费+拨备-存量
       */
      taxRiskHistory?: number
      /**
       * 利润-存量
       */
      profitHistory?: number
      /**
       * 投放额
       */
      payAmountFeature?: number
      /**
       * 收入-新增
       */
      incomeFeature?: number
      /**
       * 成本-新增
       */
      costFeature?: number
      /**
       * 差价-新增
       */
      diffFeature?: number
      /**
       * 税费+拨备-新增
       */
      taxRiskFeature?: number
      /**
       * 利润-新增
       */
      profitFeature?: number
      /**
       * 收益率水平（IRR）
       */
      irrFeature?: number
      /**
       * 年化手续费率
       */
      consultingFeeRateYearFeature?: number
      /**
       * 手续费率
       */
      consultingFeeRateFeature?: number
      /**
       * 资金成本（FTP）
       */
      ftpFeature?: number
      /**
       * 收入-合计
       */
      incomeTotal?: number
      /**
       * 利润-合计
       */
      profitTotal?: number
      /**
       * 费用-合计
       */
      expenseTotal?: number
      /**
       * 目标利润-合计
       */
      profitGoalTotal?: number
      /**
       * 目标利润（拨备前）-合计
       */
      profitGoalWithoutRiskFundTotal?: number
      /**
       * 年初/月初资产总额-合计
       */
      beginOfThisPeriodBalance?: number
      /**
       * 年末/月末资产总额-合计
       */
      endOfThisPeriodBalance?: number
    }[]
  }[]
  /**
   * 合计行数据
   */
  sumData?: {
    isSumRow?: boolean
    /**
     * FTP行业分类
     */
    ftpIndustryCategory?: string
    /**
     * FTP行业分类-展示文案
     */
    ftpIndustryCategoryDisplay?: string
    /**
     * 收入-存量
     */
    incomeHistory?: number
    /**
     * 成本-存量
     */
    costHistory?: number
    /**
     * 差价-存量
     */
    diffHistory?: number
    /**
     * 税费+拨备-存量
     */
    taxRiskHistory?: number
    /**
     * 利润-存量
     */
    profitHistory?: number
    /**
     * 投放额
     */
    payAmountFeature?: number
    /**
     * 收入-新增
     */
    incomeFeature?: number
    /**
     * 成本-新增
     */
    costFeature?: number
    /**
     * 差价-新增
     */
    diffFeature?: number
    /**
     * 税费+拨备-新增
     */
    taxRiskFeature?: number
    /**
     * 利润-新增
     */
    profitFeature?: number
    /**
     * 收益率水平（IRR）
     */
    irrFeature?: number
    /**
     * 年化手续费率
     */
    consultingFeeRateYearFeature?: number
    /**
     * 手续费率
     */
    consultingFeeRateFeature?: number
    /**
     * 资金成本（FTP）
     */
    ftpFeature?: number
    /**
     * 收入-合计
     */
    incomeTotal?: number
    /**
     * 利润-合计
     */
    profitTotal?: number
    /**
     * 费用-合计
     */
    expenseTotal?: number
    /**
     * 目标利润-合计
     */
    profitGoalTotal?: number
    /**
     * 目标利润（拨备前）-合计
     */
    profitGoalWithoutRiskFundTotal?: number
    /**
     * 年初/月初资产总额-合计
     */
    beginOfThisPeriodBalance?: number
    /**
     * 年末/月末资产总额-合计
     */
    endOfThisPeriodBalance?: number
  }
}

/**
 * 接口 [预算管理-预算计划-利润预算-汇总（其他类型）↗](http://yapi.zswltec.com:3000/project/11/interface/api/34183) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/summaryOther`
 * @更新时间 `2025-05-22 17:11:36`
 */
export interface ProfitSummaryOtherRequest {
  /**
   * 利润预算id
   */
  budgetPlanProfitId: number
  /**
   * 部门id
   */
  belongDeptId?: number
}

/**
 * 接口 [预算管理-预算计划-利润预算-汇总（其他类型）↗](http://yapi.zswltec.com:3000/project/11/interface/api/34183) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/summaryOther`
 * @更新时间 `2025-05-22 17:11:36`
 */
export type ProfitSummaryOtherResponse = {
  /**
   * 年份
   */
  year?: string
  /**
   * 部门数据
   */
  deptDataList?: {
    /**
     * 部门id
     */
    belongDeptId?: number
    /**
     * 部门名称
     */
    belongDeptName?: string
    /**
     * 指标名称
     */
    metricName?: string
    /**
     * 公用事业
     */
    publicUtilities?: number
    /**
     * 民生消费
     */
    civilConsumption?: number
    /**
     * 国有产业
     */
    stateOwnedIndustry?: number
    /**
     * 其他产业
     */
    otherIndustry?: number
    /**
     * 部门小计
     */
    sum?: number
  }[]
}[]

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-存量↗](http://yapi.zswltec.com:3000/project/11/interface/api/33853) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/history`
 * @更新时间 `2025-05-15 10:08:04`
 */
export interface DetailHistoryRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-存量↗](http://yapi.zswltec.com:3000/project/11/interface/api/33853) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/history`
 * @更新时间 `2025-05-15 10:08:04`
 */
export type DetailHistoryResponse = {
  /**
   * 部门id
   */
  belongDeptId?: number
  /**
   * 部门名称
   */
  belongDeptName?: string
  /**
   * 上月末/上年末业务余额
   */
  lastPeriodBalance?: number
  /**
   * 营业收入（不含税）
   */
  incomeWithoutTax?: number
  /**
   * 营业成本（不含税）
   */
  costWithoutTax?: number
  /**
   * 增值税
   */
  valueAddedTax?: number
  /**
   * 税金及附加
   */
  taxOther?: number
  /**
   * 当月/当年平均资金占用
   */
  averageOccupyThisPeriod?: number
  /**
   * 风险准备金
   */
  riskFund?: number
  /**
   * 考核利润
   */
  profit?: number
  /**
   * 本月末/本年末资产余额
   */
  thisPeriodBalance?: number
}[]

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/33901) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/feature`
 * @更新时间 `2025-05-21 16:24:04`
 */
export interface DetailFeatureRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/33901) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/feature`
 * @更新时间 `2025-05-21 16:24:04`
 */
export type DetailFeatureResponse = {
  /**
   * 部门id
   */
  belongDeptId?: number
  /**
   * 部门名称
   */
  belongDeptName?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
  /**
   * FTP行业分类-展示
   */
  ftpIndustryCategoryDisplay?: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify?: string
  /**
   * 风控行业分类-展示
   */
  riskControlIndustryClassifyDisplay?: string
  /**
   * 租赁类型
   */
  leaseType?: string
  /**
   * 租赁类型-展示
   */
  leaseTypeDisplay?: string
  /**
   * 预计投放日
   */
  planPayDate?: string
  /**
   * 租赁期限（月）
   */
  termMonth?: number
  /**
   * 还款周期
   */
  repayFrequency?: string
  /**
   * 还款周期-展示
   */
  repayFrequencyDisplay?: string
  /**
   * 投放额
   */
  payAmount?: number
  /**
   * 保证金比例
   */
  depositRate?: number
  /**
   * 合同利率
   */
  contractInterestRate?: number
  /**
   * IRR
   */
  irr?: number
  /**
   * XIRR
   */
  xirr?: number
  /**
   * 咨询费率
   */
  consultingFeeRate?: number
  /**
   * 年化咨询费率
   */
  consultingFeeRateYear?: number
  /**
   * FTP
   */
  ftp?: number
  /**
   * 营业收入（不含税）
   */
  incomeWithoutTax?: number
  /**
   * 营业成本（不含税）
   */
  costWithoutTax?: number
  /**
   * 增值税
   */
  valueAddedTax?: number
  /**
   * 税金及附加
   */
  taxOther?: number
  /**
   * 当月/当年平均资金占用
   */
  averageOccupyThisPeriod?: number
  /**
   * 风险准备金
   */
  riskFund?: number
  /**
   * 考核利润
   */
  profit?: number
  /**
   * 本月末/本年末资产余额
   */
  thisPeriodBalance?: number
}[]

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-部门详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/34141) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/dept`
 * @更新时间 `2025-05-22 10:35:06`
 */
export interface DetailDeptRequest {
  /**
   * 利润预算id
   */
  budgetPlanProfitId: number
  /**
   * 部门id
   */
  belongDeptId: number
  /**
   * 客户名称
   */
  clientName?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-部门详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/34141) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/dept`
 * @更新时间 `2025-05-22 10:35:06`
 */
export type DetailDeptResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 部门id
   */
  belongDeptId?: number
  /**
   * 部门名称
   */
  belongDeptName?: string
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 借据编号
   */
  receiptCode?: string
  /**
   * 业务类型（租赁类型）
   */
  leaseType?: string
  /**
   * 业务类型（租赁类型）-展示
   */
  leaseTypeDisplay?: string
  /**
   * 起租日
   */
  payDate?: string
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
  /**
   * FTP行业分类-展示
   */
  ftpIndustryCategoryDisplay?: string
  /**
   * 上年末/上月末业务余额
   */
  endOfLastPeriodBalance?: number
  /**
   * 本年末/本月末业务余额
   */
  endOfThisPeriodBalance?: number
  /**
   * 收入
   */
  income?: number
  /**
   * 成本
   */
  cost?: number
  /**
   * 毛利
   */
  grossProfit?: number
  /**
   * 上年末/上月末风险准备金余额
   */
  endOfLastPeriodRiskFund?: number
  /**
   * 本年末/本月末风险准备金余额
   */
  endOfThisPeriodRiskFund?: number
  /**
   * 累计风险准备金计提/转回
   */
  riskFundDiff?: number
  /**
   * 累计价差
   */
  diff?: number
  /**
   * 附加税
   */
  additionalTax?: number
  /**
   * 印花税
   */
  stampTax?: number
  /**
   * 累计利润
   */
  assessmentProfit?: number
  /**
   * 累计利润（扣费后）
   */
  assessmentProfitWithoutExpense?: number
  /**
   * 累计利润（非负）
   */
  assessmentProfitNonnegative?: number
  /**
   * 累计利润（扣费后）（非负数）
   */
  assessmentProfitWithoutExpenseNonnegative?: number
  /**
   * 项目利润调整项
   */
  profitAdjust?: number
  /**
   * 备注说明
   */
  remark?: string
}[]

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-部门详情-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/34147) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/dept/modify`
 * @更新时间 `2025-05-22 09:19:00`
 */
export interface DeptModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 利润调整项
   */
  profitAdjust?: number
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-预算明细-部门详情-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/34147) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/detail/dept/modify`
 * @更新时间 `2025-05-22 09:19:00`
 */
export interface DeptModifyResponse {
  /**
   * 是否成功
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 提示信息
   */
  msg?: string
  /**
   * 异常时返回的异常信息
   */
  description?: string
  /**
   * 不阻断操作流程的toast提示
   */
  toast?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-预算确认↗](http://yapi.zswltec.com:3000/project/11/interface/api/33841) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/confirm`
 * @更新时间 `2025-05-14 19:46:03`
 */
export interface ProfitConfirmRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-利润预算-预算确认↗](http://yapi.zswltec.com:3000/project/11/interface/api/33841) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-利润预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4526)
 * @请求头 `POST /budget/plan/profit/confirm`
 * @更新时间 `2025-05-14 19:46:03`
 */
export interface ProfitConfirmResponse {
  /**
   * 是否成功
   */
  success?: boolean
  /**
   * 状态码
   */
  code?: number
  /**
   * 提示信息
   */
  msg?: string
  /**
   * 异常时返回的异常信息
   */
  description?: string
  /**
   * 不阻断操作流程的toast提示
   */
  toast?: string
}

/* prettier-ignore-end */
