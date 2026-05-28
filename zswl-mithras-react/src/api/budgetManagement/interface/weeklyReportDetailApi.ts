/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改预算管理-投放计划（月度）-项目周报-详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/32797) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/pay/weekly/report/detail/modify`
 * @更新时间 `2025-05-07 10:54:21`
 */
export interface DetailModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 业务来源
   */
  projSource?: string
  /**
   * ftp行业分类
   */
  ftpIndustryCategory?: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify?: string
  /**
   * 业务类型（租赁类型）
   */
  leaseType?: string
  /**
   * 项目主办
   */
  sponsorUserId?: number
  /**
   * 业务部门
   */
  belongDeptId?: number
  /**
   * 评估主体id
   */
  evaluationSubjectId?: number
  /**
   * 国家
   */
  country?: string
  /**
   * 省份
   */
  province?: string
  /**
   * 城市
   */
  city?: string
  /**
   * 区、县
   */
  district?: string
  /**
   * 授信金额
   */
  creditAmount?: number
  /**
   * 计划投放日
   */
  planPayDate?: string
  /**
   * 已投放金额
   */
  paidAmount?: number
  /**
   * 拟投放金额
   */
  planPayAmount?: number
  /**
   * 期限
   */
  termMonth?: number
  /**
   * 项目保证金
   */
  deposit?: number
  /**
   * 合同利率
   */
  contractInterestRate?: number
  /**
   * irr
   */
  irr?: number
  /**
   * ftp
   */
  ftp?: number
  /**
   * 本周项目计划节点
   */
  planActionThisWeek?: string
  /**
   * 本周项目实际节点
   */
  actualActionThisWeek?: string
  /**
   * 下周项目计划节点
   */
  planActionNextWeek?: string
  /**
   * 项目进展状态
   */
  projectProgress?: string
  /**
   * 运营优先级
   */
  yunyingPriority?: number
  /**
   * 是否纳入资金计划
   */
  bringIntoFundPlan?: string
  /**
   * 意见及反馈
   */
  suggestion?: string
}

/**
 * 接口 [修改预算管理-投放计划（月度）-项目周报-详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/32797) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/pay/weekly/report/detail/modify`
 * @更新时间 `2025-05-07 10:54:21`
 */
export interface DetailModifyResponse {
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32845) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/pageList`
 * @更新时间 `2025-05-15 15:30:00`
 */
export interface ReportPageListRequest {
  /**
   * 项目周报id
   */
  budgetPlanWeeklyReportId?: number
  /**
   * 部门id
   */
  deptId?: number
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
  /**
   * 租赁类型
   */
  leaseType?: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
  /**
   * 投放日-起
   */
  payDateFrom?: string
  /**
   * 投放日-止
   */
  payDateTo?: string
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32845) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/pageList`
 * @更新时间 `2025-05-15 15:30:00`
 */
export interface ReportPageListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 投放计划明细id
     */
    id?: number
    /**
     * 最后一次操作人id
     */
    lastOperateUserId?: number
    /**
     * 预算计划id
     */
    budgetPlanId?: number
    /**
     * 投放计划id
     */
    budgetPlanPayId?: number
    /**
     * 项目评审id
     */
    projReviewId?: number
    /**
     * 项目编号
     */
    projCode?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 关联合同数量
     */
    contractCount?: number
    /**
     * 业务来源
     */
    projSource?: string
    /**
     * ftp行业分类
     */
    ftpIndustryCategory?: string
    /**
     * 风控行业分类
     */
    riskControlIndustryClassify?: string
    /**
     * 业务类型（租赁类型）
     */
    leaseTypes?: string
    /**
     * 项目主办id
     */
    sponsorUserId?: number
    /**
     * 项目主办名称
     */
    sponsorUserName?: string
    /**
     * 业务部门id
     */
    belongDeptId?: number
    /**
     * 业务部门名称
     */
    belongDeptName?: string
    /**
     * 评估主体id
     */
    evaluationSubjectId?: number
    /**
     * 省份
     */
    province?: string
    /**
     * 省份名称
     */
    provinceName?: string
    /**
     * 城市
     */
    city?: string
    /**
     * 城市名称
     */
    cityName?: string
    /**
     * 区、县
     */
    district?: string
    /**
     * 区、县名称
     */
    districtName?: string
    /**
     * 授信金额
     */
    creditAmount?: number
    /**
     * 计划投放日
     */
    planPayDate?: string
    /**
     * 已投放金额
     */
    paidAmount?: number
    /**
     * 拟投放金额
     */
    planPayAmount?: number
    /**
     * 期限
     */
    termMonth?: number
    /**
     * 项目保证金
     */
    deposit?: number
    /**
     * 合同利率
     */
    contractInterestRate?: number
    /**
     * irr
     */
    irr?: number
    /**
     * ftp
     */
    ftp?: number
    /**
     * 本周项目计划节点
     */
    planActionThisWeek?: string
    /**
     * 本周项目实际节点
     */
    actualActionThisWeek?: string
    /**
     * 下周项目计划节点
     */
    planActionNextWeek?: string
    /**
     * 项目进展状态
     */
    projectProgress?: string
    /**
     * 系统流程阶段
     */
    rzyProcessStage?: string
    /**
     * 运营优先级
     */
    yunyingPriority?: number
    /**
     * 是否纳入资金计划
     */
    bringIntoFundPlan?: string
    /**
     * 意见及反馈
     */
    suggestion?: string
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
  others?: {
    KEY?: {}
  }
}

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-列表-合同信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/33145) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/detail/pageList/contract`
 * @更新时间 `2025-05-15 15:30:18`
 */
export interface PageListContractRequest {
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-列表-合同信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/33145) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/detail/pageList/contract`
 * @更新时间 `2025-05-15 15:30:18`
 */
export type PageListContractResponse = {
  /**
   * 投放计划明细id
   */
  id?: number
  /**
   * 最后一次操作人id
   */
  lastOperateUserId?: number
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 投放计划id
   */
  budgetPlanPayId?: number
  /**
   * 项目评审id
   */
  projReviewId?: number
  /**
   * 项目编号
   */
  projCode?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 关联合同数量
   */
  contractCount?: number
  /**
   * 业务来源
   */
  projSource?: string
  /**
   * ftp行业分类
   */
  ftpIndustryCategory?: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify?: string
  /**
   * 业务类型（租赁类型）
   */
  leaseTypes?: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
  /**
   * 项目主办名称
   */
  sponsorUserName?: string
  /**
   * 业务部门id
   */
  belongDeptId?: number
  /**
   * 业务部门名称
   */
  belongDeptName?: string
  /**
   * 评估主体id
   */
  evaluationSubjectId?: number
  /**
   * 省份
   */
  province?: string
  /**
   * 省份名称
   */
  provinceName?: string
  /**
   * 城市
   */
  city?: string
  /**
   * 城市名称
   */
  cityName?: string
  /**
   * 区、县
   */
  district?: string
  /**
   * 区、县名称
   */
  districtName?: string
  /**
   * 授信金额
   */
  creditAmount?: number
  /**
   * 计划投放日
   */
  planPayDate?: string
  /**
   * 已投放金额
   */
  paidAmount?: number
  /**
   * 拟投放金额
   */
  planPayAmount?: number
  /**
   * 期限
   */
  termMonth?: number
  /**
   * 项目保证金
   */
  deposit?: number
  /**
   * 合同利率
   */
  contractInterestRate?: number
  /**
   * irr
   */
  irr?: number
  /**
   * ftp
   */
  ftp?: number
  /**
   * 本周项目计划节点
   */
  planActionThisWeek?: string
  /**
   * 本周项目实际节点
   */
  actualActionThisWeek?: string
  /**
   * 下周项目计划节点
   */
  planActionNextWeek?: string
  /**
   * 项目进展状态
   */
  projectProgress?: string
  /**
   * 系统流程阶段
   */
  rzyProcessStage?: string
  /**
   * 运营优先级
   */
  yunyingPriority?: number
  /**
   * 是否纳入资金计划
   */
  bringIntoFundPlan?: string
  /**
   * 意见及反馈
   */
  suggestion?: string
}[]

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/33985) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/pay/weekly/report/detail/batchDelete`
 * @更新时间 `2025-05-15 16:23:22`
 */
export interface DetailBatchDeleteRequest {
  /**
   * 业务数据id列表
   */
  ids: number[]
}

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/33985) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/pay/weekly/report/detail/batchDelete`
 * @更新时间 `2025-05-15 16:23:22`
 */
export interface DetailBatchDeleteResponse {
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/32863) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/detail/add`
 * @更新时间 `2025-05-08 09:25:38`
 */
export interface DetailAddRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 项目评审id
   */
  projReviewId: number
}

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/32863) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/detail/add`
 * @更新时间 `2025-05-08 09:25:38`
 */
export interface DetailAddResponse {
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/32851) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/detail/statistics`
 * @更新时间 `2025-05-15 15:30:00`
 */
export interface DetailStatisticsRequest {
  /**
   * 项目周报id
   */
  budgetPlanWeeklyReportId?: number
  /**
   * 部门id
   */
  deptId?: number
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
  /**
   * 租赁类型
   */
  leaseType?: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
  /**
   * 投放日-起
   */
  payDateFrom?: string
  /**
   * 投放日-止
   */
  payDateTo?: string
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/32851) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/detail/statistics`
 * @更新时间 `2025-05-15 15:30:00`
 */
export interface DetailStatisticsResponse {
  /**
   * 上月末业务余额
   */
  endOfLastMonthBalanceTotal?: number
  /**
   * 月度新增投放额
   */
  newActualPayThisMonthTotal?: number
  /**
   * 加权平均IRR
   */
  priorityAverageIrr?: number
  /**
   * 营业收入合计（不含税）
   */
  incomeWithoutTaxTotal?: number
  /**
   * 考核利润合计
   */
  profitTotal?: number
  /**
   * 考核利润合计(扣费后)
   */
  profitWithoutExpenseTotal?: number
  /**
   * 本月末业务余额
   */
  endOfThisMonthBalanceTotal?: number
}

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/32857) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/detail/modify`
 * @更新时间 `2025-05-08 09:25:38`
 */
export interface DetailModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 拟投放金额
   */
  planPayAmount?: number
  /**
   * 期限
   */
  termMonth?: number
  /**
   * 项目保证金
   */
  deposit?: number
  /**
   * 项目手续费
   */
  commission?: number
  /**
   * ftp
   */
  ftp?: number
  /**
   * 本周项目计划节点
   */
  planActionThisWeek?: string
  /**
   * 本周项目实际节点
   */
  actualActionThisWeek?: string
  /**
   * 下周项目计划节点
   */
  planActionNextWeek?: string
  /**
   * 项目进展状态
   */
  projectProgress?: string
  /**
   * 系统流程阶段
   */
  rzyProcessStage?: string
  /**
   * 运营进度反馈
   */
  yunyingFeedback?: string
  /**
   * 运营优先级
   */
  yunyingPriority?: number
  /**
   * 是否纳入资金计划
   */
  bringIntoFundPlan?: string
  /**
   * 意见及反馈
   */
  suggestion?: string
}

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/32857) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/report/detail/modify`
 * @更新时间 `2025-05-08 09:25:38`
 */
export interface DetailModifyResponse {
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-部门确认情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/33991) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/detail/deptConfirm/list`
 * @更新时间 `2025-05-15 16:23:22`
 */
export interface DeptConfirmListRequest {
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
 * 接口 [预算管理-投放计划（月度）-项目周报-明细-部门确认情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/33991) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/weekly/detail/deptConfirm/list`
 * @更新时间 `2025-05-15 16:23:22`
 */
export type DeptConfirmListResponse = {
  /**
   * 业务部门id
   */
  bizDeptId?: number
  /**
   * 业务部门名称
   */
  bizDeptName?: string
  /**
   * 归属业务部门的数量
   */
  count?: number
  /**
   * 部门负责人是否确认
   */
  isBusinessheadConfirm?: number
}[]

/**
 * 接口 [预算管理-投放计划（月度）-项目周报-详情列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32791) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/pay/weekly/report/detail/list`
 * @更新时间 `2025-05-15 15:31:57`
 */
export interface DetailListRequest {
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 部门id
   */
  deptId?: number
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
  /**
   * 租赁类型
   */
  leaseType?: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
  /**
   * 投放日-起
   */
  payDateFrom?: string
  /**
   * 投放日-止
   */
  payDateTo?: string
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
 * 接口 [预算管理-投放计划（月度）-项目周报-详情列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32791) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划（月度）-项目周报-详情-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4616)
 * @请求头 `POST /budget/plan/pay/weekly/report/detail/list`
 * @更新时间 `2025-05-15 15:31:57`
 */
export interface DetailListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 投放计划明细id
     */
    id?: number
    /**
     * 最后一次操作人id
     */
    lastOperateUserId?: number
    /**
     * 预算计划id
     */
    budgetPlanId?: number
    /**
     * 投放计划id
     */
    budgetPlanPayId?: number
    /**
     * 项目评审id
     */
    projReviewId?: number
    /**
     * 项目编号
     */
    projCode?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 业务来源
     */
    projSource?: string
    /**
     * ftp行业分类
     */
    ftpIndustryCategory?: string
    /**
     * 风控行业分类
     */
    riskControlIndustryClassify?: string
    /**
     * 业务类型（租赁类型）
     */
    leaseTypes?: string
    /**
     * 项目主办id
     */
    sponsorUserId?: number
    /**
     * 项目主办名称
     */
    sponsorUserName?: string
    /**
     * 业务部门id
     */
    belongDeptId?: number
    /**
     * 业务部门名称
     */
    belongDeptName?: string
    /**
     * 评估主体id
     */
    evaluationSubjectId?: number
    /**
     * 省份
     */
    province?: string
    /**
     * 省份名称
     */
    provinceName?: string
    /**
     * 城市
     */
    city?: string
    /**
     * 城市名称
     */
    cityName?: string
    /**
     * 区、县
     */
    district?: string
    /**
     * 区、县名称
     */
    districtName?: string
    /**
     * 授信金额
     */
    creditAmount?: number
    /**
     * 计划投放日
     */
    planPayDate?: string
    /**
     * 已投放金额
     */
    paidAmount?: number
    /**
     * 拟投放金额
     */
    planPayAmount?: number
    /**
     * 期限
     */
    termMonth?: number
    /**
     * 项目保证金
     */
    deposit?: number
    /**
     * 合同利率
     */
    contractInterestRate?: number
    /**
     * irr
     */
    irr?: number
    /**
     * ftp
     */
    ftp?: number
    /**
     * 本周项目计划节点
     */
    planActionThisWeek?: string
    /**
     * 本周项目实际节点
     */
    actualActionThisWeek?: string
    /**
     * 下周项目计划节点
     */
    planActionNextWeek?: string
    /**
     * 项目进展状态
     */
    projectProgress?: string
    /**
     * 系统流程阶段
     */
    rzyProcessStage?: string
    /**
     * 运营优先级
     */
    yunyingPriority?: number
    /**
     * 是否纳入资金计划
     */
    bringIntoFundPlan?: string
    /**
     * 意见及反馈
     */
    suggestion?: string
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
  others?: {
    KEY?: {}
  }
}

/* prettier-ignore-end */
