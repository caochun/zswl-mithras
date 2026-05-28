/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [下载现金流计划表模板↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1512) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/download`
 * @更新时间 `2022-09-22 18:56:01`
 */
export interface CashflowplanDownloadRequest {}

/**
 * 接口 [下载现金流计划表模板↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1512) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/download`
 * @更新时间 `2022-09-22 18:56:01`
 */
export interface CashflowplanDownloadResponse {}

/**
 * 接口 [导出现金流表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6960) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/cashflow/export`
 * @更新时间 `2022-09-22 18:56:01`
 */
export interface CashflowExportRequest {
  projReviewId?: number
}

/**
 * 接口 [导出现金流表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6960) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/cashflow/export`
 * @更新时间 `2022-09-22 18:56:01`
 */
export interface CashflowExportResponse {}

/**
 * 接口 [导出租金表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6964) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/rent/export`
 * @更新时间 `2022-09-22 18:56:01`
 */
export interface RentExportRequest {
  projReviewId?: number
}

/**
 * 接口 [导出租金表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6964) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/rent/export`
 * @更新时间 `2022-09-22 18:56:01`
 */
export interface RentExportResponse {}

/**
 * 接口 [上传现金流计划表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1504) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/upload`
 * @更新时间 `2022-09-07 16:32:31`
 */
export interface CashflowplanUploadRequest {
  file: File
  projReviewId: string
}

/**
 * 接口 [上传现金流计划表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1504) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/upload`
 * @更新时间 `2022-09-07 16:32:31`
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
 * 接口 [自动生成现金流计划表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1500) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/cashflow/generate`
 * @更新时间 `2023-01-03 10:05:36`
 */
export interface CashflowGenerateRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [自动生成现金流计划表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1500) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/cashflow/generate`
 * @更新时间 `2023-01-03 10:05:36`
 */
export interface CashflowGenerateResponse {
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
 * 接口 [获取现金流计划表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1508) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/list`
 * @更新时间 `2023-01-03 10:05:36`
 */
export interface CashflowplanListRequest {
  /**
   * 项目评审记录id
   */
  projReviewId: number
}

/**
 * 接口 [获取现金流计划表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/1508) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/list`
 * @更新时间 `2023-01-03 10:05:36`
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

/**
 * 接口 [计算IRR↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11317) 的 **请求类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/irr/calculate`
 * @更新时间 `2023-01-03 10:05:36`
 */
export interface IrrCalculateRequest {
  /**
   * 业务数据主键id
   */
  id: number
}

/**
 * 接口 [计算IRR↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11317) 的 **返回类型**
 *
 * @分类 [项目评审-现金流计划相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_314)
 * @请求头 `POST /proj/review/cashflowplan/irr/calculate`
 * @更新时间 `2023-01-03 10:05:36`
 */
export interface IrrCalculateResponse {
  /**
   * irr
   */
  irr?: string
  /**
   * 测算结果详情文件id
   */
  fileId?: number
}

/* prettier-ignore-end */
