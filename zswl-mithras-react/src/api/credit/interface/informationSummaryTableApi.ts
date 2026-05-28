/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改征信报告-信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38257) 的 **请求类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/modify`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 查询编号
   */
  creditCode?: number
  /**
   * 征信报告基本表id
   */
  creditReportId?: number
  /**
   * 首次有信贷交易年份
   */
  firstCredityear?: number
  /**
   * 信贷交易机构数
   */
  creditOrganizationNumber?: number
  /**
   * 未结清信贷交易机构数
   */
  unsettledCreditOrganizationNumber?: number
  /**
   * 首次有相关还款责任的年份
   */
  firstRepaymentResponsibilityYear?: number
  /**
   * 借贷交易-余额
   */
  loanTransactionBalance?: number
  /**
   * 借贷交易-被追偿余额
   */
  loanTransactionRecoveryBalance?: number
  /**
   * 借贷交易-关注类余额
   */
  loanTransactionFocusBalance?: number
  /**
   * 借贷交易-不良类余额
   */
  loanTransactionBadBalance?: number
  /**
   * 担保交易-余额
   */
  guaranteeTransactionBalance?: number
  /**
   * 担保交易-关注类余额
   */
  guaranteeTransactionFocusBalance?: number
  /**
   * 担保交易-不良类余额
   */
  guaranteeTransactionBadBalance?: number
  /**
   * 非信贷交易账户数
   */
  nonCreditTransactionNumber?: number
  /**
   * 欠税记录条数
   */
  taxArrearsRecordsNumber?: number
  /**
   * 民事判决记录条数
   */
  civilJudgmentRecordsNumber?: number
  /**
   * 强制执行记录条数
   */
  mandatoryExecutionRecordsNumber?: number
  /**
   * 行政处罚记录条数
   */
  administrativePenaltyRecordsNumber?: number
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [修改征信报告-信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38257) 的 **返回类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/modify`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryModifyResponse {
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
 * 接口 [删除征信报告-信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38245) 的 **请求类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/remove`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除征信报告-信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38245) 的 **返回类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/remove`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryRemoveResponse {
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
 * 接口 [征信报告-信息概要表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38239) 的 **请求类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/list`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryListRequest {
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
 * 接口 [征信报告-信息概要表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38239) 的 **返回类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/list`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 查询编号
     */
    creditCode?: number
    /**
     * 征信报告基本表id
     */
    creditReportId?: number
    /**
     * 首次有信贷交易年份
     */
    firstCredityear?: number
    /**
     * 信贷交易机构数
     */
    creditOrganizationNumber?: number
    /**
     * 未结清信贷交易机构数
     */
    unsettledCreditOrganizationNumber?: number
    /**
     * 首次有相关还款责任的年份
     */
    firstRepaymentResponsibilityYear?: number
    /**
     * 借贷交易-余额
     */
    loanTransactionBalance?: number
    /**
     * 借贷交易-被追偿余额
     */
    loanTransactionRecoveryBalance?: number
    /**
     * 借贷交易-关注类余额
     */
    loanTransactionFocusBalance?: number
    /**
     * 借贷交易-不良类余额
     */
    loanTransactionBadBalance?: number
    /**
     * 担保交易-余额
     */
    guaranteeTransactionBalance?: number
    /**
     * 担保交易-关注类余额
     */
    guaranteeTransactionFocusBalance?: number
    /**
     * 担保交易-不良类余额
     */
    guaranteeTransactionBadBalance?: number
    /**
     * 非信贷交易账户数
     */
    nonCreditTransactionNumber?: number
    /**
     * 欠税记录条数
     */
    taxArrearsRecordsNumber?: number
    /**
     * 民事判决记录条数
     */
    civilJudgmentRecordsNumber?: number
    /**
     * 强制执行记录条数
     */
    mandatoryExecutionRecordsNumber?: number
    /**
     * 行政处罚记录条数
     */
    administrativePenaltyRecordsNumber?: number
    /**
     * 逻辑删除，0-未删除，1-已删除
     */
    deleted?: number
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
 * 接口 [征信报告-信息概要表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/38317) 的 **请求类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/detail`
 * @更新时间 `2025-11-28 15:01:06`
 */
export interface SummaryDetailRequest {
  /**
   * 征信报告基本表id
   */
  creditReportId: number
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
 * 接口 [征信报告-信息概要表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/38317) 的 **返回类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/detail`
 * @更新时间 `2025-11-28 15:01:06`
 */
export interface SummaryDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 查询编号
   */
  creditCode?: number
  /**
   * 征信报告基本表id
   */
  creditReportId?: number
  /**
   * 首次有信贷交易年份
   */
  firstCredityear?: number
  /**
   * 信贷交易机构数
   */
  creditOrganizationNumber?: number
  /**
   * 未结清信贷交易机构数
   */
  unsettledCreditOrganizationNumber?: number
  /**
   * 首次有相关还款责任的年份
   */
  firstRepaymentResponsibilityYear?: number
  /**
   * 借贷交易-余额
   */
  loanTransactionBalance?: number
  /**
   * 借贷交易-被追偿余额
   */
  loanTransactionRecoveryBalance?: number
  /**
   * 借贷交易-关注类余额
   */
  loanTransactionFocusBalance?: number
  /**
   * 借贷交易-不良类余额
   */
  loanTransactionBadBalance?: number
  /**
   * 担保交易-余额
   */
  guaranteeTransactionBalance?: number
  /**
   * 担保交易-关注类余额
   */
  guaranteeTransactionFocusBalance?: number
  /**
   * 担保交易-不良类余额
   */
  guaranteeTransactionBadBalance?: number
  /**
   * 非信贷交易账户数
   */
  nonCreditTransactionNumber?: number
  /**
   * 欠税记录条数
   */
  taxArrearsRecordsNumber?: number
  /**
   * 民事判决记录条数
   */
  civilJudgmentRecordsNumber?: number
  /**
   * 强制执行记录条数
   */
  mandatoryExecutionRecordsNumber?: number
  /**
   * 行政处罚记录条数
   */
  administrativePenaltyRecordsNumber?: number
}

/**
 * 接口 [新增征信报告-信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38251) 的 **请求类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/add`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryAddRequest {
  /**
   * 查询编号
   */
  creditCode?: number
  /**
   * 征信报告基本表id
   */
  creditReportId?: number
  /**
   * 首次有信贷交易年份
   */
  firstCredityear?: number
  /**
   * 信贷交易机构数
   */
  creditOrganizationNumber?: number
  /**
   * 未结清信贷交易机构数
   */
  unsettledCreditOrganizationNumber?: number
  /**
   * 首次有相关还款责任的年份
   */
  firstRepaymentResponsibilityYear?: number
  /**
   * 借贷交易-余额
   */
  loanTransactionBalance?: number
  /**
   * 借贷交易-被追偿余额
   */
  loanTransactionRecoveryBalance?: number
  /**
   * 借贷交易-关注类余额
   */
  loanTransactionFocusBalance?: number
  /**
   * 借贷交易-不良类余额
   */
  loanTransactionBadBalance?: number
  /**
   * 担保交易-余额
   */
  guaranteeTransactionBalance?: number
  /**
   * 担保交易-关注类余额
   */
  guaranteeTransactionFocusBalance?: number
  /**
   * 担保交易-不良类余额
   */
  guaranteeTransactionBadBalance?: number
  /**
   * 非信贷交易账户数
   */
  nonCreditTransactionNumber?: number
  /**
   * 欠税记录条数
   */
  taxArrearsRecordsNumber?: number
  /**
   * 民事判决记录条数
   */
  civilJudgmentRecordsNumber?: number
  /**
   * 强制执行记录条数
   */
  mandatoryExecutionRecordsNumber?: number
  /**
   * 行政处罚记录条数
   */
  administrativePenaltyRecordsNumber?: number
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [新增征信报告-信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38251) 的 **返回类型**
 *
 * @分类 [征信报告-信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5450)
 * @请求头 `POST /credit/report/summary/add`
 * @更新时间 `2025-11-28 10:12:17`
 */
export interface SummaryAddResponse {
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
