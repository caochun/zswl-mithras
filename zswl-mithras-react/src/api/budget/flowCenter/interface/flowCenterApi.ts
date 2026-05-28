/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [导出收款业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/4963) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/export`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface BusinessExportRequest {
  /**
   * 应收日期
   */
  planCollectionDate?: string
}

/**
 * 接口 [导出收款业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/4963) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/export`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface BusinessExportResponse {}

/**
 * 接口 [付款业务流水列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/475) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/payment/list`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface PaymentListRequest {
  /**
   * 计划付款日期-从
   */
  applyPaymentDateFrom?: string
  /**
   * 计划付款日期-到
   */
  applyPaymentDateTo?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 核销状态
   */
  writeOffStatusList?: string[]
  writeOffStatus?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 现金流项目 PaymentFlowItemEnum
   */
  cashFlowItemList?: string[]
  cashFlowItem?: string
  /**
   * 业务部门id
   */
  bizDeptId?: number
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
 * 接口 [付款业务流水列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/475) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/payment/list`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface PaymentListResponse {
  /**
   * 查询集合
   */
  list?: {
    paymentId?: number
    /**
     * 核销状态
     */
    writeOffStatus?: string
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 现金流项目 PaymentFlowItemEnum
     */
    cashFlowItem?: string
    bizDeptId?: number
    /**
     * 业务部门
     */
    bizDept?: string
    /**
     * 应付
     */
    paymentAmount?: number
    /**
     * 已付
     */
    paidAmount?: number
    /**
     * 应付日期
     */
    applyPaymentDate?: string
    /**
     * 最新一笔付款日期，无核销不显示
     */
    paidInDate?: string
    /**
     * 付款申请编号
     */
    paymentCode?: string
    /**
     * 项目名称
     */
    projName?: string
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
 * 接口 [付款业务流水结算明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/481) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/payment/settle/detail`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface SettleDetailRequest {
  /**
   * 现金流项目 PaymentFlowItemEnum
   */
  cashFlowItem: string
  /**
   * 现金流项目
   */
  paymentId: number
}

/**
 * 接口 [付款业务流水结算明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/481) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/payment/settle/detail`
 * @更新时间 `2024-06-27 20:35:39`
 */
export type SettleDetailResponse = {
  /**
   * 现金流项目
   */
  id: number
  /**
   * 付款方式
   */
  paymentMethod: string
  /**
   * 实付金额
   */
  paidInAmount: number
  /**
   * 实付日期
   */
  paidInDate?: string
  /**
   * 票据code
   */
  billCode: string
  /**
   * 票据金额
   */
  billAmount: number
  /**
   * 票据到期日期
   */
  billExpireDate: string
  /**
   * 核销方式
   */
  writeOffType?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 创建人id
   */
  updateBy?: number
  /**
   * 创建人名称
   */
  updateByName?: string
  /**
   * 交易明细编号
   */
  bankDetailNo?: string
}[]

/**
 * 接口 [付款手工核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/469) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/payment/manual/record`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface ManualRecordRequest {
  /**
   * 现金流项目
   */
  paymentId: number
  /**
   * 现金流项目 PaymentFlowItemEnum
   */
  cashFlowItem: string
  /**
   * 付款方式
   */
  paymentMethod: string
  /**
   * 实付金额
   */
  paidInAmount: number
  /**
   * 实付日期
   */
  paidInDate: string
  billManagementAddREQ?: {
    /**
     * 收款/付款明细ID
     */
    mainId: number
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 票据类型 收款/付款 枚举-BillTypeEnum
     */
    billType: string
    /**
     * 票据code
     */
    billCode: string
    /**
     * 票据金额
     */
    billAmount: number
    /**
     * 票据到期日期
     */
    billExpireDate: string
    /**
     * 票据买入价
     */
    billBuyRate?: number
    /**
     * 票据买入价类型,0其他，1，同项目FTP
     */
    billBuyRateType?: number
  }
}

/**
 * 接口 [付款手工核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/469) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/payment/manual/record`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface ManualRecordResponse {
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
 * 接口 [导出收款业务流水列表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/4969) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/export/list`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface ExportListRequest {
  /**
   * 应收日期
   */
  planCollectionDate?: string
}

/**
 * 接口 [导出收款业务流水列表详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/4969) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/export/list`
 * @更新时间 `2024-06-27 20:35:39`
 */
export type ExportListResponse = {
  /**
   * id
   */
  collectionId?: number
  /**
   * 核销状态
   */
  writeOffStatus?: string
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 期项
   */
  phase?: number
  /**
   * 现金流项目
   */
  cashFlowItem?: string
  bizDeptId?: number
  /**
   * 业务部门
   */
  bizDept?: string
  /**
   * 计划收款日期
   */
  planCollectionDate?: string
  /**
   * 计划收款金额
   */
  planCollectionAmount?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 最新一笔收款日期，无核销不显示
   */
  collectionDate?: string
  /**
   * 实收金额
   */
  collectionAmount?: number
  /**
   * 收款核销编号
   */
  code?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 业务类型。租赁、保理、转租赁
   */
  bizType?: string
  /**
   * 租赁类型。直租、回租、经营性租赁
   */
  leaseType?: string
}[]

/**
 * 接口 [收款业务流水列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/487) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/collection/list`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface CollectionListRequest {
  /**
   * 计划收款日期-从
   */
  planCollectionDateFrom?: string
  /**
   * 计划收款日期-到
   */
  planCollectionDateTo?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 核销状态
   */
  writeOffStatusList?: string[]
  writeOffStatus?: string
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 现金流项目
   */
  cashFlowItemList?: string[]
  /**
   * 业务部门id
   */
  bizDeptId?: number
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
 * 接口 [收款业务流水列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/487) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/collection/list`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface CollectionListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    collectionId?: number
    /**
     * 核销状态
     */
    writeOffStatus?: string
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 期项
     */
    phase?: number
    /**
     * 现金流项目
     */
    cashFlowItem?: string
    bizDeptId?: number
    /**
     * 业务部门
     */
    bizDept?: string
    /**
     * 计划收款日期
     */
    planCollectionDate?: string
    /**
     * 计划收款金额
     */
    planCollectionAmount?: number
    /**
     * 本金
     */
    principal?: number
    /**
     * 利息
     */
    interest?: number
    /**
     * 最新一笔收款日期，无核销不显示
     */
    collectionDate?: string
    /**
     * 实收金额
     */
    collectionAmount?: number
    /**
     * 收款核销编号
     */
    code?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 业务类型。租赁、保理、转租赁
     */
    bizType?: string
    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    leaseType?: string
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
 * 接口 [收款业务流水结算明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/499) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/collection/settle/detail`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface SettleDetailRequest {
  /**
   * 收款
   */
  collectionId: number
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
 * 接口 [收款业务流水结算明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/499) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/collection/settle/detail`
 * @更新时间 `2024-06-27 20:35:39`
 */
export type SettleDetailResponse = {
  /**
   * ID
   */
  id?: number
  /**
   * 收款类型
   */
  collectionType?: string
  /**
   * 实收日期
   */
  collectionDate?: string
  /**
   * 实收金额
   */
  collectionAmount?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 罚息
   */
  penaltyInterest?: number
  /**
   * 票据code
   */
  billCode?: string
  /**
   * 票据金额
   */
  billAmount?: number
  /**
   * 票据到期日期
   */
  billExpireDate?: string
  /**
   * 票据买入价
   */
  billBuyRate?: number
  /**
   * 核销方式
   */
  writeOffType?: string
  /**
   * 核销状态
   */
  writeOffStatus?: string
  /**
   * 更新时间
   */
  updateTime?: string
  /**
   * 创建人id
   */
  updateBy?: number
  /**
   * 创建人名称
   */
  updateByName?: string
  /**
   * 交易明细编号
   */
  bankDetailNo?: string
}[]

/**
 * 接口 [收款手工核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/493) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/collection/manual/record`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface ManualRecordRequest {
  /**
   * 现金流项目
   */
  collectionId: string
  /**
   * 收款编号
   */
  collectionCode: string
  /**
   * 现金流项目
   */
  cashFlowItem: string
  /**
   * 收款类型
   */
  collectionType: string
  /**
   * 实收日期
   */
  collectionDate: string
  /**
   * 实际收款金额
   */
  collectionAmount?: number
  /**
   * 本金
   */
  principal?: number
  /**
   * 利息
   */
  interest?: number
  /**
   * 罚息
   */
  penaltyInterest?: number
  billManagementAddREQ?: {
    /**
     * 收款/付款明细ID
     */
    mainId: number
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 票据类型 收款/付款 枚举-BillTypeEnum
     */
    billType: string
    /**
     * 票据code
     */
    billCode: string
    /**
     * 票据金额
     */
    billAmount: number
    /**
     * 票据到期日期
     */
    billExpireDate: string
    /**
     * 票据买入价
     */
    billBuyRate?: number
    /**
     * 票据买入价类型,0其他，1，同项目FTP
     */
    billBuyRateType?: number
  }
}

/**
 * 接口 [收款手工核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/493) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/business/collection/manual/record`
 * @更新时间 `2024-06-27 20:35:39`
 */
export interface ManualRecordResponse {
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
 * 接口 [流水中心统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/1135) 的 **请求类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/count`
 * @更新时间 `2024-03-07 09:25:28`
 */
export interface CenterCountRequest {}

/**
 * 接口 [流水中心统计↗](http://yapi.zswltec.com:3000/project/11/interface/api/1135) 的 **返回类型**
 *
 * @分类 [收款核销明细-流水中心接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_164)
 * @请求头 `POST /collection/flow/center/count`
 * @更新时间 `2024-03-07 09:25:28`
 */
export interface CenterCountResponse {
  total?: number
}

/* prettier-ignore-end */
