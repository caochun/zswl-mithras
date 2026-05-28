/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [预算管理-预算计划-投放计划-明细-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/32425) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/detail/batchDelete`
 * @更新时间 `2025-04-15 22:42:50`
 */
export interface DetailBatchDeleteRequest {
  /**
   * 业务数据id列表
   */
  ids: number[]
}

/**
 * 接口 [预算管理-预算计划-投放计划-明细-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/32425) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/detail/batchDelete`
 * @更新时间 `2025-04-15 22:42:50`
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
 * 接口 [预算管理-预算计划-投放计划-明细-部门确认情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/32413) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/detail/deptConfirm/list`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DeptConfirmListRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 部门id
   */
  belongDeptId?: number
}

/**
 * 接口 [预算管理-预算计划-投放计划-明细-部门确认情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/32413) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/detail/deptConfirm/list`
 * @更新时间 `2025-05-23 15:35:23`
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
  /**
   * 分管领导是否确认
   */
  isLeaderinchargeConfirm?: number
}[]

/**
 * 接口 [预算管理-预算计划-投放计划-月度-明细-保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/32467) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/save`
 * @更新时间 `2025-04-16 09:51:00`
 */
export interface DetailSaveRequest {
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-保存↗](http://yapi.zswltec.com:3000/project/11/interface/api/32467) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/save`
 * @更新时间 `2025-04-16 09:51:00`
 */
export interface DetailSaveResponse {
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32419) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/pageList`
 * @更新时间 `2025-06-17 11:55:46`
 */
export interface DetailPageListRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 部门id
   */
  belongDeptId?: number
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
  planPayDateFrom?: string
  /**
   * 投放日-止
   */
  planPayDateTo?: string
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32419) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/pageList`
 * @更新时间 `2025-06-17 11:55:46`
 */
export interface DetailPageListResponse {
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
     * 关联合同数量
     */
    contractCount?: number
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
    leaseType?: string
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
     * 咨询服务费
     */
    consultingFee?: number
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
     * irr-ftp差值
     */
    irrFtpDiff?: number
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-列表-合同信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/34231) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/pageList/contract`
 * @更新时间 `2025-06-17 11:55:46`
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-列表-合同信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/34231) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/pageList/contract`
 * @更新时间 `2025-06-17 11:55:46`
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
   * 关联合同数量
   */
  contractCount?: number
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
  leaseType?: string
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
   * 咨询服务费
   */
  consultingFee?: number
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
   * irr-ftp差值
   */
  irrFtpDiff?: number
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
}[]

/**
 * 接口 [预算管理-预算计划-投放计划-月度-明细-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/32809) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/add`
 * @更新时间 `2025-05-07 14:14:01`
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/32809) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/add`
 * @更新时间 `2025-05-07 14:14:01`
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/32407) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/statistics`
 * @更新时间 `2025-06-17 11:55:46`
 */
export interface DetailStatisticsRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 部门id
   */
  belongDeptId?: number
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
  planPayDateFrom?: string
  /**
   * 投放日-止
   */
  planPayDateTo?: string
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/32407) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/statistics`
 * @更新时间 `2025-06-17 11:55:46`
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/32815) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/modify`
 * @更新时间 `2025-06-17 11:55:46`
 */
export interface DetailModifyRequest {
  /**
   * id
   */
  id: number
  /**
   * 租赁类型
   */
  leaseType?: string
  /**
   * 拟投放金额
   */
  planPayAmount?: number
  /**
   * 投放日
   */
  planPayDate?: string
  /**
   * 期限
   */
  termMonth?: number
  /**
   * 项目保证金
   */
  deposit?: number
  /**
   * 咨询服务费
   */
  consultingFee?: number
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/32815) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/modify`
 * @更新时间 `2025-06-17 11:55:46`
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
 * 接口 [预算管理-预算计划-投放计划-月度-明细-选择项目评审↗](http://yapi.zswltec.com:3000/project/11/interface/api/34585) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/projreview/list`
 * @更新时间 `2025-06-17 11:55:46`
 */
export interface ProjreviewListRequest {}

/**
 * 接口 [预算管理-预算计划-投放计划-月度-明细-选择项目评审↗](http://yapi.zswltec.com:3000/project/11/interface/api/34585) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/month/detail/projreview/list`
 * @更新时间 `2025-06-17 11:55:46`
 */
export type ProjreviewListResponse = {
  key?: string
  value?: number
}[]

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32431) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/pageList`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailPageListRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 部门id
   */
  belongDeptId?: number
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
  actualPayFrom?: string
  /**
   * 投放日-止
   */
  actualPayTo?: string
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32431) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/pageList`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailPageListResponse {
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
     * 部门id
     */
    belongDeptId?: number
    /**
     * 部门名称
     */
    belongDeptName?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * FTP行业分类
     */
    ftpIndustryCategory?: string
    /**
     * 业务类型（租赁类型）
     */
    leaseType?: string
    /**
     * 项目主办id
     */
    sponsorUserId?: number
    /**
     * 项目主办名称
     */
    sponsorUserName?: string
    /**
     * 项目金额
     */
    projectAmount?: number
    /**
     * 首期租金率
     */
    firstRentRate?: number
    /**
     * 租赁期限
     */
    termMonth?: number
    /**
     * 保证金率
     */
    depositRate?: number
    /**
     * 还款频率
     */
    repayFrequency?: string
    /**
     * 咨询费率
     */
    consultingFeeRate?: number
    /**
     * 还款期数
     */
    repayTimesTotal?: number
    /**
     * 手续费率
     */
    commissionRate?: number
    /**
     * 支付方式
     */
    payType?: string
    /**
     * 名义价款
     */
    nominalPrice?: number
    /**
     * 利息计算方式
     */
    interestCalculateWay?: string
    /**
     * 合同利率类型
     */
    contractInterestRateType?: string
    /**
     * 合同利率
     */
    contractInterestRate?: number
    /**
     * 投放日
     */
    payDate?: string
    /**
     * irr
     */
    irr?: number
    /**
     * ftp
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
     * 税金及附加
     */
    taxAndOther?: number
    /**
     * 风险准备金
     */
    riskReserve?: number
    /**
     * 考核利润
     */
    profit?: number
    /**
     * 费用
     */
    expense?: number
    /**
     * 考核利润（扣费后）
     */
    profitWithoutExpense?: number
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-基本信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/32437) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/baseInfo`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailBaseInfoRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-基本信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/32437) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/baseInfo`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailBaseInfoResponse {
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * FTP行业分类
   */
  ftpIndustryCategory?: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify?: string
  /**
   * 租赁类型
   */
  leaseType?: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
  /**
   * 项目主办名称
   */
  sponsorUserName?: string
  /**
   * 业务负责人id
   */
  bizDeptLeaderId?: number
  /**
   * 业务负责人名称
   */
  bizDeptLeaderName?: string
  /**
   * 部门id
   */
  belongDeptId?: number
  /**
   * 部门名称
   */
  belongDeptName?: string
}

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-复制↗](http://yapi.zswltec.com:3000/project/11/interface/api/32521) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/copy`
 * @更新时间 `2025-05-09 17:46:08`
 */
export interface DetailCopyRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-复制↗](http://yapi.zswltec.com:3000/project/11/interface/api/32521) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/copy`
 * @更新时间 `2025-05-09 17:46:08`
 */
export type DetailCopyResponse = number

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-报价方案↗](http://yapi.zswltec.com:3000/project/11/interface/api/32443) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/price`
 * @更新时间 `2025-05-14 11:28:11`
 */
export interface DetailPriceRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-报价方案↗](http://yapi.zswltec.com:3000/project/11/interface/api/32443) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/price`
 * @更新时间 `2025-05-14 11:28:11`
 */
export interface DetailPriceResponse {
  /**
   * 项目金额
   */
  projectAmount?: number
  /**
   * 首期租金率
   */
  firstRentRate?: number
  /**
   * 租赁期限
   */
  termMonth?: number
  /**
   * 保证金率
   */
  depositRate?: number
  /**
   * 还款频率
   */
  repayFrequency?: string
  /**
   * 咨询费率
   */
  consultingFeeRate?: number
  /**
   * 还款期数
   */
  repayTimesTotal?: number
  /**
   * 手续费率
   */
  commissionRate?: number
  /**
   * 支付方式
   */
  payType?: string
  /**
   * 名义价款
   */
  nominalPrice?: number
  /**
   * 利息计算方式
   */
  interestCalculateWay?: string
  /**
   * 合同利率类型
   */
  contractInterestRateType?: string
  /**
   * 合同利率
   */
  contractInterestRate?: number
  /**
   * 投放日
   */
  payDate?: string
  /**
   * irr
   */
  irr?: number
}

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-收入分摊情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/32455) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/incomeSharing`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailIncomeSharingRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-收入分摊情况↗](http://yapi.zswltec.com:3000/project/11/interface/api/32455) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/incomeSharing`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailIncomeSharingResponse {
  /**
   * 表头行
   */
  headerList?: string[]
  /**
   * 数据行
   */
  dataList?: {}[][]
  /**
   * FTP价格
   */
  ftp?: number
}

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-期间费用↗](http://yapi.zswltec.com:3000/project/11/interface/api/32461) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/expense`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailExpenseRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-期间费用↗](http://yapi.zswltec.com:3000/project/11/interface/api/32461) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/expense`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailExpenseResponse {
  /**
   * 表头行
   */
  headerList?: string[]
  /**
   * 数据行
   */
  dataList?: {}[][]
  /**
   * FTP价格
   */
  ftp?: number
}

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-测算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32515) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/calculate`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailCalculateRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 投放计划明细id
   */
  budgetPlanPayDetailId?: number
  /**
   * 客户名称
   */
  clientName: string
  /**
   * FTP行业分类
   */
  ftpIndustryCategory: string
  /**
   * 风控行业分类
   */
  riskControlIndustryClassify: string
  /**
   * 业务类型（租赁类型）
   */
  leaseType: string
  /**
   * 项目主办id
   */
  sponsorUserId?: number
  /**
   * 业务负责人id
   */
  bizDeptLeaderId?: number
  /**
   * 业务部门id
   */
  belongDeptId?: number
  /**
   * 项目金额
   */
  projectAmount: number
  /**
   * 首期租金率
   */
  firstRentRate?: number
  /**
   * 租赁期限
   */
  termMonth: number
  /**
   * 保证金率
   */
  depositRate?: number
  /**
   * 还款频率
   */
  repayFrequency?: string
  /**
   * 咨询费率
   */
  consultingFeeRate?: number
  /**
   * 还款期数
   */
  repayTimesTotal?: number
  /**
   * 手续费率
   */
  commissionRate?: number
  /**
   * 支付方式
   */
  payType?: string
  /**
   * 名义价款
   */
  nominalPrice?: number
  /**
   * 利息计算方式
   */
  interestCalculateWay?: string
  /**
   * 合同利率类型
   */
  contractInterestRateType: string
  /**
   * 合同利率
   */
  contractInterestRate: number
  /**
   * 投放日
   */
  payDate: string
  /**
   * IRR
   */
  irr?: number
}

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-测算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32515) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/calculate`
 * @更新时间 `2025-05-23 15:35:23`
 */
export type DetailCalculateResponse = number

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-现金流计划↗](http://yapi.zswltec.com:3000/project/11/interface/api/32449) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/cashFlow`
 * @更新时间 `2025-04-15 16:29:34`
 */
export interface DetailCashFlowRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-现金流计划↗](http://yapi.zswltec.com:3000/project/11/interface/api/32449) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/cashFlow`
 * @更新时间 `2025-04-15 16:29:34`
 */
export type DetailCashFlowResponse = {
  /**
   * 日期
   */
  cashFlowDate?: string
  /**
   * 期项
   */
  cashFlowPhase?: number
  /**
   * 现金流金额
   */
  cashFlowAmount?: number
  /**
   * 租金
   */
  rent?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 剩余本金
   */
  remainingPrincipal?: number
}[]

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/32401) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/statistics`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailStatisticsRequest {
  /**
   * 投放计划id
   */
  budgetPlanPayId: number
  /**
   * 部门id
   */
  belongDeptId?: number
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
  actualPayFrom?: string
  /**
   * 投放日-止
   */
  actualPayTo?: string
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/32401) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/statistics`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailStatisticsResponse {
  /**
   * 上年末业务余额
   */
  endOfLastYearBalanceTotal?: number
  /**
   * 年度新增投放额
   */
  newActualPayThisYearTotal?: number
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
   * 本年末业务余额
   */
  endOfThisYearBalanceTotal?: number
}

/**
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-资金成本↗](http://yapi.zswltec.com:3000/project/11/interface/api/32509) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/fundCost`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailFundCostRequest {
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
 * 接口 [预算管理-预算计划-投放计划-非月度-明细-资金成本↗](http://yapi.zswltec.com:3000/project/11/interface/api/32509) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4538)
 * @请求头 `POST /budget/plan/pay/notmonth/detail/fundCost`
 * @更新时间 `2025-05-23 15:35:23`
 */
export interface DetailFundCostResponse {
  /**
   * 表头行
   */
  headerList?: string[]
  /**
   * 数据行
   */
  dataList?: {}[][]
  /**
   * FTP价格
   */
  ftp?: number
}

/* prettier-ignore-end */
