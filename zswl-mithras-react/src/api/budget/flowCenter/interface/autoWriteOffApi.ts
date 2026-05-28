/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [\/capital\/write\/off\/release\/bank\/flow↗](http://yapi.zswltec.com:3000/project/11/interface/api/23281) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/release/bank/flow`
 * @更新时间 `2024-10-08 16:07:37`
 */
export interface ReleasebankFlowRequest {
  /**
   * 选择的流水列表
   */
  bankFlowIds: number[]
  /**
   * 批次号
   */
  batchNumber: string
}

/**
 * 接口 [\/capital\/write\/off\/release\/bank\/flow↗](http://yapi.zswltec.com:3000/project/11/interface/api/23281) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/release/bank/flow`
 * @更新时间 `2024-10-08 16:07:37`
 */
export interface ReleasebankFlowResponse {
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
 * 接口 [修改业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21781) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/update/business/flow`
 * @更新时间 `2024-09-29 09:36:34`
 */
export interface UpdatebusinessFlowRequest {
  /**
   * 业务流水id
   */
  id: number
  /**
   * TabId
   */
  financeFlowTabMainInfoId: number
  /**
   * 本次核销金额
   */
  thisWriteOffAmount: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [修改业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21781) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/update/business/flow`
 * @更新时间 `2024-09-29 09:36:34`
 */
export interface UpdatebusinessFlowResponse {
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
 * 接口 [删除业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21787) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/delete/business/flow`
 * @更新时间 `2024-09-29 09:55:59`
 */
export interface DeletebusinessFlowRequest {
  /**
   * 业务流水ID
   */
  businessFlowIdList: number[]
  /**
   * 当前tab的ID
   */
  financeFlowTabMainInfoId: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [删除业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21787) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/delete/business/flow`
 * @更新时间 `2024-09-29 09:55:59`
 */
export interface DeletebusinessFlowResponse {
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
 * 接口 [删除单个Tab↗](http://yapi.zswltec.com:3000/project/11/interface/api/22705) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/delete/tab`
 * @更新时间 `2024-09-29 10:36:44`
 */
export interface OffdeleteTabRequest {
  /**
   * 当前tab的ID
   */
  financeFlowTabMainInfoId?: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [删除单个Tab↗](http://yapi.zswltec.com:3000/project/11/interface/api/22705) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/delete/tab`
 * @更新时间 `2024-09-29 10:36:44`
 */
export interface OffdeleteTabResponse {
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
 * 接口 [删除流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21763) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/delete/bank/flow`
 * @更新时间 `2024-09-27 17:42:10`
 */
export interface DeletebankFlowRequest {
  /**
   * 流水id列表
   */
  flowIdList?: number[]
  /**
   * 当前的tab的ID
   */
  financeFlowTabMainInfoId: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [删除流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21763) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/delete/bank/flow`
 * @更新时间 `2024-09-27 17:42:10`
 */
export interface DeletebankFlowResponse {
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
 * 接口 [增加流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21769) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/add/bank/flow`
 * @更新时间 `2024-09-27 17:42:10`
 */
export interface AddbankFlowRequest {
  /**
   * 流水id列表
   */
  flowIdList?: number[]
  /**
   * 当前的tab的ID
   */
  financeFlowTabMainInfoId: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [增加流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21769) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/add/bank/flow`
 * @更新时间 `2024-09-27 17:42:10`
 */
export interface AddbankFlowResponse {
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
 * 接口 [导入流水前的校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/21751) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/check/before/import`
 * @更新时间 `2024-10-08 14:43:05`
 */
export interface CheckbeforeImportRequest {
  /**
   * 前置天数
   */
  plusDays: number
  /**
   * 选择的流水列表
   */
  bankFlowIds: number[]
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [导入流水前的校验↗](http://yapi.zswltec.com:3000/project/11/interface/api/21751) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/check/before/import`
 * @更新时间 `2024-10-08 14:43:05`
 */
export interface CheckbeforeImportResponse {
  /**
   * 未选中的银行流水列表
   */
  unSelectedBankFlowMap?: {}
}

/**
 * 接口 [手动核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/21745) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/manual/write/off`
 * @更新时间 `2024-09-19 20:04:51`
 */
export interface ManualwriteOffRequest {
  /**
   * 批次号
   */
  batchNumber: string
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [手动核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/21745) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/manual/write/off`
 * @更新时间 `2024-09-19 20:04:51`
 */
export interface ManualwriteOffResponse {
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
 * 接口 [新增业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21775) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/add/business/flow`
 * @更新时间 `2024-09-29 10:25:03`
 */
export interface AddbusinessFlowRequest {
  /**
   * 当前tab的ID
   */
  financeFlowTabMainInfoId: number
  /**
   * 客户ID/机构ID
   */
  clientOrOrgId?: number
  /**
   * 合同编号/融资编号
   */
  contractCodeOrFinancingCode?: string
  /**
   * 现金流项目
   */
  cashFlowItem?: string
  /**
   * 现金流编号
   */
  cashFlowCode?: string
  /**
   * 应收时间/应付时间
   */
  shouldPayTime?: string
  /**
   * 应收金额/应付金额
   */
  shouldWriteOffAmount?: number
  /**
   * 未收金额/未付金额
   */
  noWriteOffAmount?: number
  /**
   * 本次核销金额
   */
  thisWriteOffAmount?: number
  /**
   * 配置一个源表ID，自动核销使用，业务功能可忽略
   */
  sourceId?: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [新增业务流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/21775) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/add/business/flow`
 * @更新时间 `2024-09-29 10:25:03`
 */
export interface AddbusinessFlowResponse {
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
 * 接口 [获取单个Tab信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/23107) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/single/tab`
 * @更新时间 `2024-10-08 15:29:12`
 */
export interface OffsingleTabRequest {
  /**
   * 当前tab的ID
   */
  financeFlowTabMainInfoId: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [获取单个Tab信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/23107) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/single/tab`
 * @更新时间 `2024-10-08 15:29:12`
 */
export interface OffsingleTabResponse {
  /**
   * tab主键ID
   */
  id?: number
  /**
   * tab名称
   */
  tabName?: string
  /**
   * 批次号
   */
  batchNumber?: string
  /**
   * 流水结果列表
   */
  bankFlowList?: {
    /**
     * id
     */
    id?: number
    /**
     * 流水状态
     */
    flowStatus?: string
    /**
     * 交易明细编号
     */
    transactionDetailsNumber?: string
    /**
     * 资金组织
     */
    financialOrganization?: string
    /**
     * 银行账号
     */
    bankAccount?: string
    /**
     * 开户银行
     */
    bankName?: string
    /**
     * 币别
     */
    currency?: string
    /**
     * 交易时间
     */
    transactionDate?: string
    /**
     * 摘要
     */
    mainInfo?: string
    /**
     * 收款金额
     */
    collectionAmount?: number
    /**
     * 付款金额
     */
    paymentAmount?: number
    /**
     * 余额
     */
    depositAmount?: number
    /**
     * 手续费
     */
    handingFees?: number
    /**
     * 对方户名
     */
    otherName?: string
    /**
     * 对方账号
     */
    otherBankAccount?: string
    /**
     * 对方开户行
     */
    otherBankName?: string
    /**
     * 明细流水号
     */
    detailSerialNumber?: string
    /**
     * 数据来源
     */
    dataSource?: string
    /**
     * 最后更新时间
     */
    updateTime?: string
    /**
     * 提示标签【苍穹已删除】
     */
    promptLabel?: string
    /**
     * 剩余可核销金额
     */
    surplusAmount?: number
    /**
     * 保融流水id
     */
    cicoBruid?: string
  }[]
  /**
   * 业务流水列表
   */
  businessFlowList?: {
    /**
     * 主键ID
     */
    id?: number
    /**
     * 源表数据ID
     */
    sourceId?: number
    /**
     * 源数据名称
     */
    sourceBusinessCode?: string
    /**
     * 现金流项目
     */
    cashFlowItem?: string
    /**
     * 现金流编号
     */
    cashFlowCode?: string
    /**
     * 应付时间
     */
    shouldWriteOffTime?: string
    /**
     * 应付金额
     */
    shouldWriteOffAmount?: number
    /**
     * 未付金额
     */
    noWriteOffAmount?: number
    /**
     * 本次核销金额
     */
    thisWriteOffAmount?: number
    /**
     * 是否是系统生成
     */
    isSystemGenerate?: number
    /**
     * 业务模块
     */
    businessModel?: string
    /**
     * 关联银行流水集合表ID
     */
    financeFlowCollectionId?: number
    /**
     * 银行流水编号
     */
    bankFlowNo?: string
    /**
     * 批次号
     */
    batchNumber?: string
  }[]
}

/**
 * 接口 [选择流水之后的排序结果↗](http://yapi.zswltec.com:3000/project/11/interface/api/21757) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/flow/match/result`
 * @更新时间 `2024-10-08 14:43:05`
 */
export interface FlowmatchResultRequest {
  /**
   * 前置天数
   */
  plusDays: number
  /**
   * 选择的流水列表
   */
  bankFlowIds: number[]
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [选择流水之后的排序结果↗](http://yapi.zswltec.com:3000/project/11/interface/api/21757) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/flow/match/result`
 * @更新时间 `2024-10-08 14:43:05`
 */
export type FlowmatchResultResponse = {
  /**
   * tab主键ID
   */
  id?: number
  /**
   * tab名称
   */
  tabName?: string
  /**
   * 批次号
   */
  batchNumber?: string
  /**
   * 流水结果列表
   */
  bankFlowList?: {
    /**
     * id
     */
    id?: number
    /**
     * 流水状态
     */
    flowStatus?: string
    /**
     * 交易明细编号
     */
    transactionDetailsNumber?: string
    /**
     * 资金组织
     */
    financialOrganization?: string
    /**
     * 银行账号
     */
    bankAccount?: string
    /**
     * 开户银行
     */
    bankName?: string
    /**
     * 币别
     */
    currency?: string
    /**
     * 交易时间
     */
    transactionDate?: string
    /**
     * 摘要
     */
    mainInfo?: string
    /**
     * 收款金额
     */
    collectionAmount?: number
    /**
     * 付款金额
     */
    paymentAmount?: number
    /**
     * 余额
     */
    depositAmount?: number
    /**
     * 手续费
     */
    handingFees?: number
    /**
     * 对方户名
     */
    otherName?: string
    /**
     * 对方账号
     */
    otherBankAccount?: string
    /**
     * 对方开户行
     */
    otherBankName?: string
    /**
     * 明细流水号
     */
    detailSerialNumber?: string
    /**
     * 数据来源
     */
    dataSource?: string
    /**
     * 最后更新时间
     */
    updateTime?: string
    /**
     * 提示标签【苍穹已删除】
     */
    promptLabel?: string
    /**
     * 剩余可核销金额
     */
    surplusAmount?: number
    /**
     * 保融流水id
     */
    cicoBruid?: string
  }[]
  /**
   * 业务流水列表
   */
  businessFlowList?: {
    /**
     * 主键ID
     */
    id?: number
    /**
     * 源表数据ID
     */
    sourceId?: number
    /**
     * 源数据名称
     */
    sourceBusinessCode?: string
    /**
     * 现金流项目
     */
    cashFlowItem?: string
    /**
     * 现金流编号
     */
    cashFlowCode?: string
    /**
     * 应付时间
     */
    shouldWriteOffTime?: string
    /**
     * 应付金额
     */
    shouldWriteOffAmount?: number
    /**
     * 未付金额
     */
    noWriteOffAmount?: number
    /**
     * 本次核销金额
     */
    thisWriteOffAmount?: number
    /**
     * 是否是系统生成
     */
    isSystemGenerate?: number
    /**
     * 业务模块
     */
    businessModel?: string
    /**
     * 关联银行流水集合表ID
     */
    financeFlowCollectionId?: number
    /**
     * 银行流水编号
     */
    bankFlowNo?: string
    /**
     * 批次号
     */
    batchNumber?: string
  }[]
}[]

/**
 * 接口 [重新匹配单个Tab的信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/22387) 的 **请求类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/rematch/tab`
 * @更新时间 `2024-09-29 21:14:05`
 */
export interface OffrematchTabRequest {
  /**
   * 当前tab的ID
   */
  financeFlowTabMainInfoId: number
  /**
   * 提前N天
   */
  plusDays: number
  /**
   * 核销类型 WriteOffBusinessModelEnum#name
   */
  writeOffBusinessModel: string
}

/**
 * 接口 [重新匹配单个Tab的信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/22387) 的 **返回类型**
 *
 * @分类 [自动核销-API↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3152)
 * @请求头 `POST /capital/write/off/rematch/tab`
 * @更新时间 `2024-09-29 21:14:05`
 */
export interface OffrematchTabResponse {
  /**
   * tab主键ID
   */
  id?: number
  /**
   * tab名称
   */
  tabName?: string
  /**
   * 批次号
   */
  batchNumber?: string
  /**
   * 流水结果列表
   */
  bankFlowList?: {
    /**
     * id
     */
    id?: number
    /**
     * 流水状态
     */
    flowStatus?: string
    /**
     * 交易明细编号
     */
    transactionDetailsNumber?: string
    /**
     * 资金组织
     */
    financialOrganization?: string
    /**
     * 银行账号
     */
    bankAccount?: string
    /**
     * 开户银行
     */
    bankName?: string
    /**
     * 币别
     */
    currency?: string
    /**
     * 交易时间
     */
    transactionDate?: string
    /**
     * 摘要
     */
    mainInfo?: string
    /**
     * 收款金额
     */
    collectionAmount?: number
    /**
     * 付款金额
     */
    paymentAmount?: number
    /**
     * 余额
     */
    depositAmount?: number
    /**
     * 手续费
     */
    handingFees?: number
    /**
     * 对方户名
     */
    otherName?: string
    /**
     * 对方账号
     */
    otherBankAccount?: string
    /**
     * 对方开户行
     */
    otherBankName?: string
    /**
     * 明细流水号
     */
    detailSerialNumber?: string
    /**
     * 数据来源
     */
    dataSource?: string
    /**
     * 最后更新时间
     */
    updateTime?: string
    /**
     * 提示标签【苍穹已删除】
     */
    promptLabel?: string
    /**
     * 剩余可核销金额
     */
    surplusAmount?: number
    /**
     * 保融流水id
     */
    cicoBruid?: string
  }[]
  /**
   * 业务流水列表
   */
  businessFlowList?: {
    /**
     * 主键ID
     */
    id?: number
    /**
     * 源表数据ID
     */
    sourceId?: number
    /**
     * 源数据名称
     */
    sourceBusinessCode?: string
    /**
     * 现金流项目
     */
    cashFlowItem?: string
    /**
     * 现金流编号
     */
    cashFlowCode?: string
    /**
     * 应付时间
     */
    shouldWriteOffTime?: string
    /**
     * 应付金额
     */
    shouldWriteOffAmount?: number
    /**
     * 未付金额
     */
    noWriteOffAmount?: number
    /**
     * 本次核销金额
     */
    thisWriteOffAmount?: number
    /**
     * 是否是系统生成
     */
    isSystemGenerate?: number
    /**
     * 业务模块
     */
    businessModel?: string
    /**
     * 关联银行流水集合表ID
     */
    financeFlowCollectionId?: number
    /**
     * 银行流水编号
     */
    bankFlowNo?: string
    /**
     * 批次号
     */
    batchNumber?: string
  }[]
}

/* prettier-ignore-end */
