/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [增值税发票上传↗](http://yapi.zswltec.com:3000/project/11/interface/api/2779) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/upload`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceUploadRequest {
  /**
   * 发票id
   */
  vatInvoiceId?: string
  /**
   * 操作类型(REPLACE:替换发票)
   */
  operateType?: string
  /**
   * 租赁物id
   */
  leaseholdId: string
  /**
   * 文件列表
   */
  files: string
}

/**
 * 接口 [增值税发票上传↗](http://yapi.zswltec.com:3000/project/11/interface/api/2779) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/upload`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceUploadResponse {
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
 * 接口 [增值税发票下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/2827) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/exportExcel`
 * @更新时间 `2024-05-12 18:04:48`
 */
export interface VatInvoiceExportExcelRequest {
  /**
   * id
   */
  leaseholdId: number
  /**
   * 购买方
   */
  invoicePayerName?: string
  /**
   * 销售方
   */
  invoiceSellerName?: string
  /**
   * 开票日期始
   */
  invoiceDateStart?: string
  /**
   * 开票日期终
   */
  invoiceDateEnd?: string
  /**
   * 验真结果
   */
  verifyResult?: string
  /**
   * 发票状态
   */
  status?: string
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
 * 接口 [增值税发票下载↗](http://yapi.zswltec.com:3000/project/11/interface/api/2827) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/exportExcel`
 * @更新时间 `2024-05-12 18:04:48`
 */
export interface VatInvoiceExportExcelResponse {
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
 * 接口 [增值税发票分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/2797) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/list`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceListRequest {
  /**
   * id
   */
  leaseholdId: number
  /**
   * 购买方
   */
  invoicePayerName?: string
  /**
   * 销售方
   */
  invoiceSellerName?: string
  /**
   * 开票日期始
   */
  invoiceDateStart?: string
  /**
   * 开票日期终
   */
  invoiceDateEnd?: string
  /**
   * 验真结果
   */
  verifyResult?: string
  /**
   * 发票状态
   */
  status?: string
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
 * 接口 [增值税发票分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/2797) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/list`
 * @更新时间 `2024-05-13 21:22:21`
 */
export type VatInvoiceListResponse = {
  /**
   * 发票id
   */
  id?: number
  /**
   * 文件id
   */
  fileId?: number
  /**
   * 文件名称
   */
  fileName?: string
  /**
   * 发票号码
   */
  invoiceNo?: string
  /**
   * 开票日期
   */
  invoiceIssueDate?: string
  /**
   * 购买方
   */
  invoicePayerName?: string
  /**
   * 销售方
   */
  invoiceSellerName?: string
  /**
   * 是否盖章
   */
  existStample?: boolean
  /**
   * 验真结果
   */
  verifyResult?: string
  /**
   * 发票状态
   */
  status?: string
  /**
   * 备注
   */
  note?: string
  /**
   * 锁定内容不支持修改
   */
  locked?: boolean
  /**
   * 发票产品信息
   */
  invoiceProductList?: {
    id?: number
    /**
     * 发票产品信息id
     */
    invoiceId?: number
    /**
     * 货物或服务名称
     */
    invoiceGoods?: string
    /**
     * 规格型号
     */
    invoicePlateSpecific?: string
    /**
     * 单价明细
     */
    invoiceElectransUnit?: string
    /**
     * 数量明细
     */
    invoiceElectransQuantity?: string
    /**
     * 税率明细
     */
    invoiceTaxRate?: string
    /**
     * 税额明细
     */
    invoiceTax?: string
    /**
     * 金额明细
     */
    invoicePrice?: string
  }[]
}[]

/**
 * 接口 [增值税发票批量修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/2809) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/update`
 * @更新时间 `2024-05-12 18:04:48`
 */
export interface VatInvoiceUpdateRequest {
  /**
   * id
   */
  vatInvoiceIds: number[]
  /**
   * 发票号码
   */
  invoiceNo?: string
  /**
   * 开票日期
   */
  invoiceIssueDate?: string
  /**
   * 购买方
   */
  invoicePayerName?: string
  /**
   * 销售方
   */
  invoiceSellerName?: string
  /**
   * 开票内容（货物或服务名称）
   */
  invoiceGoods?: string
  /**
   * 规格型号
   */
  invoicePlateSpecific?: string
  /**
   * 单位
   */
  invoiceElectransUnit?: string
}

/**
 * 接口 [增值税发票批量修改↗](http://yapi.zswltec.com:3000/project/11/interface/api/2809) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/update`
 * @更新时间 `2024-05-12 18:04:48`
 */
export interface VatInvoiceUpdateResponse {
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
 * 接口 [增值税发票批量删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/2785) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/delete`
 * @更新时间 `2024-05-13 10:40:51`
 */
export interface VatInvoiceDeleteRequest {
  /**
   * 租赁物清单id
   */
  leaseholdId?: number
  /**
   * 操作类型(REPLACE:替换发票)
   */
  operateType?: string
  /**
   * id
   */
  vatInvoiceIds?: number[]
}

/**
 * 接口 [增值税发票批量删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/2785) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/delete`
 * @更新时间 `2024-05-13 10:40:51`
 */
export interface VatInvoiceDeleteResponse {
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
 * 接口 [增值税发票重新上传↗](http://yapi.zswltec.com:3000/project/11/interface/api/2791) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/anewUpload`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceAnewUploadRequest {
  /**
   * 发票id
   */
  vatInvoiceId?: string
  /**
   * 操作类型(REPLACE:替换发票)
   */
  operateType?: string
  /**
   * 租赁物id
   */
  leaseholdId: string
  /**
   * 文件列表
   */
  files: string
}

/**
 * 接口 [增值税发票重新上传↗](http://yapi.zswltec.com:3000/project/11/interface/api/2791) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/anewUpload`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceAnewUploadResponse {
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
 * 接口 [增值税发票重新验真↗](http://yapi.zswltec.com:3000/project/11/interface/api/2821) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/retest`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceRetestRequest {
  /**
   * 发票id
   */
  invoiceId?: number
  /**
   * 发票类型
   */
  invoiceType?: string
  /**
   * 发票号码
   */
  invoiceNo?: string
  /**
   * 发票代码
   */
  invoiceCode?: string
  /**
   * 开票日期
   */
  invoiceDate?: string
  /**
   * 发票验证码
   */
  verifyCode?: string
  /**
   * 发票验证码
   */
  invoiceSum?: string
  /**
   * 发票文件
   */
  multipartFiles?: {
    [k: string]: unknown
  }[]
}

/**
 * 接口 [增值税发票重新验真↗](http://yapi.zswltec.com:3000/project/11/interface/api/2821) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/retest`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceRetestResponse {
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
 * 接口 [增值税发票金额校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/2803) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/amountCheckout`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceAmountCheckoutRequest {
  /**
   * 租赁物id
   */
  leaseholdId: number
  /**
   * 流程id
   */
  processInstanceId?: string
}

/**
 * 接口 [增值税发票金额校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/2803) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/amountCheckout`
 * @更新时间 `2024-05-13 21:22:21`
 */
export type VatInvoiceAmountCheckoutResponse = string

/**
 * 接口 [统计各状态发票数量↗](http://yapi.zswltec.com:3000/project/11/interface/api/2815) 的 **请求类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/count`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceCountRequest {
  /**
   * 租赁物id
   */
  leaseholdId: number
  /**
   * 流程id
   */
  processInstanceId?: string
}

/**
 * 接口 [统计各状态发票数量↗](http://yapi.zswltec.com:3000/project/11/interface/api/2815) 的 **返回类型**
 *
 * @分类 [租赁物-增值税发票接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_680)
 * @请求头 `POST /lease/vatInvoice/count`
 * @更新时间 `2024-05-13 21:22:21`
 */
export interface VatInvoiceCountResponse {
  /**
   * 本次识别合计
   */
  total?: number
  /**
   * 识别成功张数
   */
  succeed?: number
  /**
   * 验真通过张数
   */
  pass?: number
  /**
   * 验真不通过
   */
  noPass?: number
  /**
   * 识别失败张数
   */
  fail?: number
}

/* prettier-ignore-end */
