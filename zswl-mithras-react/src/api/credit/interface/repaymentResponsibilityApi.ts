/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改征信报告-相关还款责任信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38227) 的 **请求类型**
 *
 * @分类 [征信报告-相关还款责任信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5444)
 * @请求头 `POST /credit/report/repayment/responsibility/modify`
 * @更新时间 `2025-11-28 10:11:45`
 */
export interface ResponsibilityModifyRequest {
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
   * 责任类型
   */
  responsibilityType?: string
  /**
   * 被追偿业务-还款责任金额
   */
  recoverableRepaymentResponsibilityAmount?: number
  /**
   * 被追偿业务-账户数
   */
  recoverableAccountNumber?: number
  /**
   * 被追偿业务-余额
   */
  recoverableBalance?: number
  /**
   * 其他借贷交易-还款责任金额
   */
  otherRepaymentResponsibilityAmount?: number
  /**
   * 其他借贷交易-账户数
   */
  otherAccountNumber?: number
  /**
   * 其他借贷交易-余额
   */
  otherBalance?: number
  /**
   * 其他借贷交易-关注类余额
   */
  otherFocusBalance?: number
  /**
   * 其他借贷交易-不良类余额
   */
  otherBadBalance?: number
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [修改征信报告-相关还款责任信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38227) 的 **返回类型**
 *
 * @分类 [征信报告-相关还款责任信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5444)
 * @请求头 `POST /credit/report/repayment/responsibility/modify`
 * @更新时间 `2025-11-28 10:11:45`
 */
export interface ResponsibilityModifyResponse {
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
 * 接口 [删除征信报告-相关还款责任信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38221) 的 **请求类型**
 *
 * @分类 [征信报告-相关还款责任信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5444)
 * @请求头 `POST /credit/report/repayment/responsibility/remove`
 * @更新时间 `2025-11-28 10:11:45`
 */
export interface ResponsibilityRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除征信报告-相关还款责任信息概要表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38221) 的 **返回类型**
 *
 * @分类 [征信报告-相关还款责任信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5444)
 * @请求头 `POST /credit/report/repayment/responsibility/remove`
 * @更新时间 `2025-11-28 10:11:45`
 */
export interface ResponsibilityRemoveResponse {
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
 * 接口 [征信报告-相关还款责任信息概要表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38233) 的 **请求类型**
 *
 * @分类 [征信报告-相关还款责任信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5444)
 * @请求头 `POST /credit/report/repayment/responsibility/list`
 * @更新时间 `2025-11-28 15:05:59`
 */
export interface ResponsibilityListRequest {
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
 * 接口 [征信报告-相关还款责任信息概要表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38233) 的 **返回类型**
 *
 * @分类 [征信报告-相关还款责任信息概要表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5444)
 * @请求头 `POST /credit/report/repayment/responsibility/list`
 * @更新时间 `2025-11-28 15:05:59`
 */
export interface ResponsibilityListResponse {
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
     * 责任类型 CreditReportRepaymentLiabilityEnum
     */
    responsibilityType?: string
    /**
     * 被追偿业务-还款责任金额
     */
    recoverableRepaymentResponsibilityAmount?: number
    /**
     * 被追偿业务-账户数
     */
    recoverableAccountNumber?: number
    /**
     * 被追偿业务-余额
     */
    recoverableBalance?: number
    /**
     * 其他借贷交易-还款责任金额
     */
    otherRepaymentResponsibilityAmount?: number
    /**
     * 其他借贷交易-账户数
     */
    otherAccountNumber?: number
    /**
     * 其他借贷交易-余额
     */
    otherBalance?: number
    /**
     * 其他借贷交易-关注类余额
     */
    otherFocusBalance?: number
    /**
     * 其他借贷交易-不良类余额
     */
    otherBadBalance?: number
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

/* prettier-ignore-end */
