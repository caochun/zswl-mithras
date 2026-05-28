/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改应收逾期集成表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37381) 的 **请求类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/modify`
 * @更新时间 `2025-09-19 09:56:04`
 */
export interface IntegrationModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 国有类型
   */
  ownedType?: string
  /**
   * 实控人
   */
  actualController?: string
  /**
   * 款项内容.款项内容编码
   */
  paymentNumber?: string
  /**
   * 单据账龄起算日
   */
  recordStartDate?: string
  /**
   * 单据日期
   */
  recordBillDate?: string
  /**
   * 科目
   */
  accounttypeNumber?: string
  /**
   * 约定收款日期
   */
  recordDueDate?: string
  /**
   * 约定收款条件
   */
  recordPaymentTerms?: string
  /**
   * 应收金额（元）
   */
  receAmount?: number
  /**
   * 行业正常收款周期
   */
  collectionCycle?: number
}

/**
 * 接口 [修改应收逾期集成表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37381) 的 **返回类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/modify`
 * @更新时间 `2025-09-19 09:56:04`
 */
export interface IntegrationModifyResponse {
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
 * 接口 [删除应收逾期集成表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37393) 的 **请求类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/remove`
 * @更新时间 `2025-09-19 09:56:04`
 */
export interface IntegrationRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除应收逾期集成表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37393) 的 **返回类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/remove`
 * @更新时间 `2025-09-19 09:56:04`
 */
export interface IntegrationRemoveResponse {
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
 * 接口 [应收逾期集成表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37375) 的 **请求类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/list`
 * @更新时间 `2025-09-22 09:45:46`
 */
export interface IntegrationListRequest {
  /**
   * 收款编号
   */
  collectionCode?: string
  /**
   * 任务id编号
   */
  overdueReportId: number
  /**
   * 单据状态 OverdueRecordStatueEnum
   */
  recordStatus?: string
  /**
   * 单据账龄起算日
   */
  recordStartDateFrom?: string
  /**
   * 单据账龄起算日
   */
  recordStartDateTo?: string
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
 * 接口 [应收逾期集成表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37375) 的 **返回类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/list`
 * @更新时间 `2025-09-22 09:45:46`
 */
export interface IntegrationListResponse {
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
     * 国有类型
     */
    ownedType?: string
    /**
     * 实控人
     */
    actualController?: string
    /**
     * 款项内容.款项内容编码
     */
    paymentNumber?: string
    /**
     * 单据账龄起算日
     */
    recordStartDate?: string
    /**
     * 单据日期
     */
    recordBillDate?: string
    /**
     * 科目
     */
    accounttypeNumber?: string
    /**
     * 约定收款日期
     */
    recordDueDate?: string
    /**
     * 约定收款条件
     */
    recordPaymentTerms?: string
    /**
     * 应收金额（元）
     */
    receAmount?: number
    /**
     * 行业正常收款周期
     */
    collectionCycle?: number
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
 * 接口 [推送应收逾期集成表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37387) 的 **请求类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/push`
 * @更新时间 `2025-09-19 16:17:28`
 */
export interface IntegrationPushRequest {
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
 * 接口 [推送应收逾期集成表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37387) 的 **返回类型**
 *
 * @分类 [应收逾期集成表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5306)
 * @请求头 `POST /finance/overdue/integration/push`
 * @更新时间 `2025-09-19 16:17:28`
 */
export interface IntegrationPushResponse {
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
