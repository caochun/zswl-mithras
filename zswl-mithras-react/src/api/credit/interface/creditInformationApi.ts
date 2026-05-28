/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改征信报告-未结清信贷及授信信息表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38263) 的 **请求类型**
 *
 * @分类 [征信报告-未结清信贷及授信信息表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5456)
 * @请求头 `POST /credit/report/unsettled/summary/modify`
 * @更新时间 `2025-11-28 10:12:28`
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
   * 款项模块
   */
  paymentModule?: string
  /**
   * 款项类型-短期-贴现
   */
  paymentType?: string
  /**
   * 款项分类-正常，关注-不良-合计
   */
  fundClassification?: string
  /**
   * 账户数
   */
  accountNumber?: number
  /**
   * 账户余额
   */
  accountAmount?: number
  /**
   * 逻辑删除，0-未删除，1-已删除
   */
  deleted?: number
}

/**
 * 接口 [修改征信报告-未结清信贷及授信信息表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38263) 的 **返回类型**
 *
 * @分类 [征信报告-未结清信贷及授信信息表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5456)
 * @请求头 `POST /credit/report/unsettled/summary/modify`
 * @更新时间 `2025-11-28 10:12:28`
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
 * 接口 [删除征信报告-未结清信贷及授信信息表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38275) 的 **请求类型**
 *
 * @分类 [征信报告-未结清信贷及授信信息表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5456)
 * @请求头 `POST /credit/report/unsettled/summary/remove`
 * @更新时间 `2025-11-28 10:12:28`
 */
export interface SummaryRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除征信报告-未结清信贷及授信信息表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38275) 的 **返回类型**
 *
 * @分类 [征信报告-未结清信贷及授信信息表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5456)
 * @请求头 `POST /credit/report/unsettled/summary/remove`
 * @更新时间 `2025-11-28 10:12:28`
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
 * 接口 [征信报告-未结清信贷及授信信息表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38269) 的 **请求类型**
 *
 * @分类 [征信报告-未结清信贷及授信信息表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5456)
 * @请求头 `POST /credit/report/unsettled/summary/list`
 * @更新时间 `2025-11-28 15:06:08`
 */
export interface SummaryListRequest {
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
 * 接口 [征信报告-未结清信贷及授信信息表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38269) 的 **返回类型**
 *
 * @分类 [征信报告-未结清信贷及授信信息表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5456)
 * @请求头 `POST /credit/report/unsettled/summary/list`
 * @更新时间 `2025-11-28 15:06:08`
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
     * 款项模块 CreditReportBusinessTypeEnum
     */
    paymentModule?: string
    /**
     * 款项类型-短期-贴现
     */
    paymentType?: string
    /**
     * 款项分类-正常，关注-不良-合计
     */
    fundClassification?: string
    /**
     * 账户数
     */
    accountNumber?: number
    /**
     * 账户余额
     */
    accountAmount?: number
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
