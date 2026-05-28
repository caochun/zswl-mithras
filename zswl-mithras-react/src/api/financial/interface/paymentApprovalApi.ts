/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [批量操作-付款列表下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11717) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/batchReceiptDownload`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepayBatchReceiptDownloadRequest {
  /**
   * 批次id
   */
  batchId: string
  /**
   * 下载的付款id列表
   */
  receiptIdList: string
}

/**
 * 接口 [批量操作-付款列表下载↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11717) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/batchReceiptDownload`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepayBatchReceiptDownloadResponse {}

/**
 * 接口 [创建批次↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11713) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/createBatch`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepayCreateBatchRequest {
  /**
   * 类型：批量：BATCH,自动：AUTO
   */
  batchType: string
  /**
   * 批量付款id列表
   */
  receiptIdList?: number[]
}

/**
 * 接口 [创建批次↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11713) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/createBatch`
 * @更新时间 `2023-02-20 15:14:32`
 */
export type RepayCreateBatchResponse = number

/**
 * 接口 [单个提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11714) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/submit`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepaySubmitRequest {
  /**
   * 业务数据主键id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [单个提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11714) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/submit`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepaySubmitResponse {
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
 * 接口 [批量提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11715) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/batchSubmit`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepayBatchSubmitRequest {
  /**
   * 批次id
   */
  batchId: string
  /**
   * 提交审批的付款id列表
   */
  receiptIdList: string
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [批量提交审批↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11715) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/batchSubmit`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepayBatchSubmitResponse {
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
 * 接口 [批量操作-付款列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11718) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/batchReceiptList`
 * @更新时间 `2023-02-20 15:14:32`
 */
export interface RepayBatchReceiptListRequest {
  /**
   * 业务数据主键id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [批量操作-付款列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11718) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/batchReceiptList`
 * @更新时间 `2023-02-20 15:14:32`
 */
export type RepayBatchReceiptListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 收付款编号
   */
  receiptRepayCode?: string
  /**
   * 融资机构id
   */
  financingOrgId?: number
  /**
   * 融资机构名称
   */
  financingOrgName?: string
  /**
   * 融资金额(元)
   */
  financingAmount?: number
  /**
   * 累计已还本金(元)
   */
  paidPrincipal?: number
  /**
   * 累计已还利息(元)
   */
  paidInterest?: number
  /**
   * 配套项目名称列表
   */
  projNameList?: string[]
  /**
   * 质押合同编号列表
   */
  pledgeContractCodeList?: string[]
  /**
   * 借款日期
   */
  borrowingDate?: string
  /**
   * 到期日期
   */
  expirationDate?: string
  /**
   * 本月计划还款合计(元)
   */
  planedRepayAmount?: number
  /**
   * 本月计划还款本金(元)
   */
  planedRepayPrincipal?: number
  /**
   * 本月计划还款利息(元)
   */
  planedRepayInterest?: number
  /**
   * 计划还本日
   */
  planedRepayPrincipleDate?: string
  /**
   * 计划还息日
   */
  planedRepayInterestDate?: string
}[]

/**
 * 接口 [资金收付款版本列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11768) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/version/list`
 * @更新时间 `2023-02-22 17:31:58`
 */
export interface VersionListRequest {
  /**
   * 主数据ID
   */
  mainId?: number
  /**
   * 模块类型
   */
  module?: string
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
}

/**
 * 接口 [资金收付款版本列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11768) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/version/list`
 * @更新时间 `2023-02-22 17:31:58`
 */
export interface VersionListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 主数据id
     */
    mainId?: number
    /**
     * 版本号
     */
    version?: string
    /**
     * 版本类型
     */
    type?: number
    /**
     * 是否可和上版本比较
     */
    canCompare?: number
    /**
     * 业务模块枚举
     */
    module?: string
    createTime?: string
    createBy?: number
    updateTime?: string
    updateBy?: number
    /**
     * 操作人id
     */
    operatorId?: number
    /**
     * 操作人名称
     */
    operatorName?: string
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
}

/**
 * 接口 [资金收付款版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11769) 的 **请求类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/compare/preVersion`
 * @更新时间 `2023-02-22 17:32:00`
 */
export interface ComparePreVersionRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [资金收付款版本比较详情（与上一版本比较）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11769) 的 **返回类型**
 *
 * @分类 [资金收付款审批相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2124)
 * @请求头 `POST /fund/receipt/repay/compare/preVersion`
 * @更新时间 `2023-02-22 17:32:00`
 */
export interface ComparePreVersionResponse {
  /**
   * 旧版本数据
   */
  oldData?: {
    KEY?: {}[]
  }
  /**
   * 新版本数据
   */
  newData?: {
    KEY?: {
      KEY?: {
        beforeValue?: {}
        value?: {}
        isChange?: boolean
      }
    }[]
  }
  /**
   * 模块change标志
   */
  moduleChanged?: {
    KEY?: boolean
  }
}

/* prettier-ignore-end */
