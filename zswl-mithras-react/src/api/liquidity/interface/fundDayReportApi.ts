/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [日结指标↗](http://yapi.zswltec.com:3000/project/11/interface/api/26407) 的 **请求类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/indicator/list`
 * @更新时间 `2024-12-20 16:00:49`
 */
export interface IndicatorListRequest {}

/**
 * 接口 [日结指标↗](http://yapi.zswltec.com:3000/project/11/interface/api/26407) 的 **返回类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/indicator/list`
 * @更新时间 `2024-12-20 16:00:49`
 */
export interface IndicatorListResponse {
  /**
   * 日期
   */
  date?: string
  /**
   * 日初余额
   */
  dawnOfDayBalance?: number
  /**
   * 日初非监管户余额
   */
  dawnOfDayNonSupervisionBalance?: number
  /**
   * 日初非受限余额
   */
  dawnOfDayNonRestrictedBalance?: number
  /**
   * 今日计划租金流入
   */
  todayPlanRentIncome?: number
  /**
   * 今日计划还本付息
   */
  todayPlanRepayPrincipalInterest?: number
  /**
   * 日终余额
   */
  endOfDayBalance?: number
  /**
   * 日终非监管户余额
   */
  endOfDayNonSupervisionBalance?: number
  /**
   * 日终非受限余额
   */
  endOfDayNonRestrictedBalance?: number
}

/**
 * 接口 [租金流入↗](http://yapi.zswltec.com:3000/project/11/interface/api/26395) 的 **请求类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/rent/income`
 * @更新时间 `2024-12-19 16:06:58`
 */
export interface RentIncomeRequest {
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
 * 接口 [租金流入↗](http://yapi.zswltec.com:3000/project/11/interface/api/26395) 的 **返回类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/rent/income`
 * @更新时间 `2024-12-19 16:06:58`
 */
export interface RentIncomeResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 承租人id
     */
    tenantId?: number
    /**
     * 承租人姓名
     */
    tenantName?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 本期到期日
     */
    expireDate?: string
    /**
     * 本期应还金额
     */
    shouldPayAmount?: string
    /**
     * 已还金额
     */
    paidAmount?: string
    /**
     * 未还金额
     */
    unpaidAmount?: string
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
 * 接口 [账户余额↗](http://yapi.zswltec.com:3000/project/11/interface/api/26401) 的 **请求类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/account/balance`
 * @更新时间 `2024-12-20 16:00:49`
 */
export interface AccountBalanceRequest {}

/**
 * 接口 [账户余额↗](http://yapi.zswltec.com:3000/project/11/interface/api/26401) 的 **返回类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/account/balance`
 * @更新时间 `2024-12-20 16:00:49`
 */
export interface AccountBalanceResponse {
  /**
   * 监管账户余额
   */
  supervisionAccountBalanceList?: {
    /**
     * 银行
     */
    bankName?: string
    /**
     * 日初余额
     */
    accountBalance?: number
  }[]
  /**
   * 非监管账户余额
   */
  nonSupervisionAccountBalanceList?: {
    /**
     * 银行
     */
    bankName?: string
    /**
     * 日初余额
     */
    accountBalance?: number
  }[]
}

/**
 * 接口 [还本付息↗](http://yapi.zswltec.com:3000/project/11/interface/api/26413) 的 **请求类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/repay/principalInterest`
 * @更新时间 `2024-12-19 16:06:58`
 */
export interface RepayPrincipalInterestRequest {
  /**
   * 查询日期
   */
  queryDate?: string
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
 * 接口 [还本付息↗](http://yapi.zswltec.com:3000/project/11/interface/api/26413) 的 **返回类型**
 *
 * @分类 [资金日报Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3662)
 * @请求头 `POST /fundDayReport/repay/principalInterest`
 * @更新时间 `2024-12-19 16:06:58`
 */
export interface RepayPrincipalInterestResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 融资机构名称
     */
    financingOrgName?: string[]
    /**
     * 融资编号
     */
    financingCode?: string
    /**
     * 本期到期日
     */
    expireDate?: string
    /**
     * 本期应还金额
     */
    shouldPayAmount?: string
    /**
     * 应还本金
     */
    shouldPayPrincipal?: string
    /**
     * 应还利息
     */
    shouldPayInterest?: string
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

/* prettier-ignore-end */
