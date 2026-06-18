/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改应收逾期结算表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37429) 的 **请求类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/modify`
 * @更新时间 `2025-09-19 09:56:15`
 */
export interface SettlementModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 收款明细id
   */
  collectionId?: number
  /**
   * 收款编号
   */
  collectionCode?: string
  /**
   * 单据状态
   */
  recordStatus?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 单据日期
   */
  recordBillDate?: string
  /**
   * 结算日期
   */
  settlementDate?: string
  /**
   * 结算记录的凭证记账日期
   */
  voucherAccountDate?: string
  /**
   * 结算关系
   */
  settlementRelation?: string
  /**
   * 结算金额（元）
   */
  settlementAmount?: number
}

/**
 * 接口 [修改应收逾期结算表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37429) 的 **返回类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/modify`
 * @更新时间 `2025-09-19 09:56:15`
 */
export interface SettlementModifyResponse {
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
 * 接口 [删除应收逾期结算表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37423) 的 **请求类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/remove`
 * @更新时间 `2025-09-19 09:56:15`
 */
export interface SettlementRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除应收逾期结算表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37423) 的 **返回类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/remove`
 * @更新时间 `2025-09-19 09:56:15`
 */
export interface SettlementRemoveResponse {
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
 * 接口 [应收逾期结算表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37417) 的 **请求类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/list`
 * @更新时间 `2025-09-22 09:45:54`
 */
export interface SettlementListRequest {
  /**
   * 任务id编号
   */
  overdueReportId: number
  /**
   * 收款编号
   */
  collectionCode?: string
  /**
   * 单据状态
   */
  recordStatus?: string
  /**
   * 单据账龄起算日
   */
  recordBillDateFrom?: string
  /**
   * 单据账龄起算日
   */
  recordBillDateTo?: string
  /**
   * 流程id
   */
  processInstanceId?: string
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
 * 接口 [应收逾期结算表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37417) 的 **返回类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/list`
 * @更新时间 `2025-09-22 09:45:54`
 */
export interface SettlementListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 收款明细id
     */
    collectionId?: number
    /**
     * 收款编号
     */
    collectionCode?: string
    /**
     * 单据状态 OverdueRecordStatueEnum
     */
    recordStatus?: string
    /**
     * 合同id
     */
    contractId?: number
    /**
     * 合同编号
     */
    contractCode?: string
    /**
     * 项目名称
     */
    projName?: string
    /**
     * 客户id
     */
    clientId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 单据日期
     */
    recordBillDate?: string
    /**
     * 结算日期
     */
    settlementDate?: string
    /**
     * 结算记录的凭证记账日期
     */
    voucherAccountDate?: string
    /**
     * 结算关系
     */
    settlementRelation?: string
    /**
     * 结算金额（元）
     */
    settlementAmount?: number
    /**
     * 审批状态 ProjProcessState
     */
    approvalStatus?: string
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
 * 接口 [推送应收逾期结算↗](http://yapi.zswltec.com:3000/project/11/interface/api/37447) 的 **请求类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/push`
 * @更新时间 `2025-09-19 16:17:46`
 */
export interface SettlementPushRequest {
  /**
   * 逾期报送计划id
   */
  reportBaseId: number
  /**
   * 推送id
   */
  ids?: number[]
}

/**
 * 接口 [推送应收逾期结算↗](http://yapi.zswltec.com:3000/project/11/interface/api/37447) 的 **返回类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/push`
 * @更新时间 `2025-09-19 16:17:46`
 */
export interface SettlementPushResponse {
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
 * 接口 [新增应收逾期结算表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37435) 的 **请求类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/add`
 * @更新时间 `2025-09-19 09:56:15`
 */
export interface SettlementAddRequest {
  /**
   * 收款明细id
   */
  collectionId?: number
  /**
   * 收款编号
   */
  collectionCode?: string
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  /**
   * 项目名称
   */
  projName?: string
  /**
   * 客户id
   */
  clientId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 单据日期
   */
  recordBillDate?: string
  /**
   * 结算日期
   */
  settlementDate?: string
  /**
   * 结算记录的凭证记账日期
   */
  voucherAccountDate?: string
  /**
   * 结算关系
   */
  settlementRelation?: string
  /**
   * 结算金额（元）
   */
  settlementAmount?: number
}

/**
 * 接口 [新增应收逾期结算表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37435) 的 **返回类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/add`
 * @更新时间 `2025-09-19 09:56:15`
 */
export interface SettlementAddResponse {
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
 * 接口 [逾期查询客户下合同信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37453) 的 **请求类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/contract/relation`
 * @更新时间 `2025-09-22 13:58:59`
 */
export interface ContractRelationRequest {
  /**
   * 客户id
   */
  clientId?: string
}

/**
 * 接口 [逾期查询客户下合同信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/37453) 的 **返回类型**
 *
 * @分类 [应收逾期结算表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5318)
 * @请求头 `POST /finance/overdue/settlement/contract/relation`
 * @更新时间 `2025-09-22 13:58:59`
 */
export type ContractRelationResponse = {
  /**
   * 合同ID
   */
  contractId?: number
  /**
   * 合同编号
   */
  contractCode?: string
  flowItem?: {
    /**
     * 合同ID
     */
    flowId?: number
    /**
     * 合同编号
     */
    flowCode?: string
  }[]
}[]

/* prettier-ignore-end */
