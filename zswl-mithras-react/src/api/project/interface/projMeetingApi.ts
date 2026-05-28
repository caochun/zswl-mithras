/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [导出现金流表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30079) 的 **请求类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/cashflow/export`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface CashflowExportRequest {
  /**
   * 项目评审记录id
   */
  meetMinuteId?: number
  /**
   * 项目评审记录id
   */
  projReviewId: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [导出现金流表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30079) 的 **返回类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/cashflow/export`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface CashflowExportResponse {}

/**
 * 接口 [导出租金表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30073) 的 **请求类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/rent/export`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface RentExportRequest {
  /**
   * 项目评审记录id
   */
  meetMinuteId?: number
  /**
   * 项目评审记录id
   */
  projReviewId: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [导出租金表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30073) 的 **返回类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/rent/export`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface RentExportResponse {}

/**
 * 接口 [上传现金流计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30061) 的 **请求类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/upload`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface CashflowplanUploadRequest {
  file: File
  projReviewId: string
}

/**
 * 接口 [上传现金流计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30061) 的 **返回类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/upload`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface CashflowplanUploadResponse {
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
 * 接口 [获取现金流计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30067) 的 **请求类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/list`
 * @更新时间 `2025-03-23 10:43:38`
 */
export interface CashflowplanListRequest {
  /**
   * 项目评审记录id
   */
  meetMinuteId?: number
  /**
   * 项目评审记录id
   */
  id?: number
  /**
   * 流程id
   */
  processInstanceId?: string
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [获取现金流计划表↗](http://yapi.zswltec.com:3000/project/11/interface/api/30067) 的 **返回类型**
 *
 * @分类 [项目评审-评审会会议纪要-现金流计划相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4130)
 * @请求头 `POST /proj/review/quotation/proposal/cashflowplan/list`
 * @更新时间 `2025-03-23 10:43:38`
 */
export type CashflowplanListResponse = {
  /**
   * 日期
   */
  date?: string
  /**
   * 期项
   */
  phase?: number
  /**
   * 租金(租赁、转租赁)/应收保理款(保理)/回收款(债权转让)，单位：毫厘
   */
  rent?: number
  /**
   * 现金流金额，单位：毫厘
   */
  cashFlowAmount?: number
  /**
   * 本金，单位：毫厘
   */
  principal?: number
  /**
   * 利息，单位：毫厘
   */
  interest?: number
  /**
   * 剩余本金，单位：毫厘
   */
  remainingPrincipal?: number
  /**
   * id
   */
  id?: number
}[]

/* prettier-ignore-end */
