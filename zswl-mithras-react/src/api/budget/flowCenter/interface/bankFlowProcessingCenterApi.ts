/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [业务流水-资金端列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3427) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /finance/list`
 * @更新时间 `2024-05-22 14:40:29`
 */
export interface FinanceListRequest {
  /**
   * 核销状态
   */
  writeOffStatus?: string
  /**
   * 日期开始
   */
  dateBegin?: string
  /**
   * 日期结束
   */
  dateEnd?: string
  /**
   * 融资渠道
   */
  financingRoute?: string
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
 * 接口 [业务流水-资金端列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3427) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /finance/list`
 * @更新时间 `2024-05-22 14:40:29`
 */
export interface FinanceListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * idKey
     */
    idKey?: string
    /**
     * 核销状态
     */
    writeOffStatus?: string
    /**
     * 应收/付款类型
     */
    type?: string
    /**
     * 现金流项目
     */
    cashFlowItem?: string
    /**
     * 金额
     */
    amount?: number
    /**
     * 日期
     */
    date?: string
    /**
     * 流水ID
     */
    serialNo?: string
    /**
     * 融资渠道
     */
    financingRoute?: string
    /**
     * 融资金额
     */
    financingAmount?: number
    /**
     * 业务类型
     */
    businessType?: string
    /**
     * 子表
     */
    subTables?: {
      /**
       * 应收/付款类型
       */
      type?: string
      /**
       * 现金流项目
       */
      cashFlowItem?: string
      /**
       * 金额
       */
      amount?: number
      /**
       * 日期
       */
      date?: string
      /**
       * 流水ID
       */
      serialNo?: string
    }[]
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
 * 接口 [删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/3229) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/delete`
 * @更新时间 `2024-05-30 19:15:21`
 */
export interface CenterDeleteRequest {
  /**
   * 流水的ID列表
   */
  financeFlowIds: number[]
}

/**
 * 接口 [删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/3229) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/delete`
 * @更新时间 `2024-05-30 19:15:21`
 */
export interface CenterDeleteResponse {
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
 * 接口 [手动拉取资金流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/3217) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/manual/pull/flow`
 * @更新时间 `2024-06-11 15:55:29`
 */
export interface PullFlowRequest {
  /**
   * 开始时间
   */
  beginTime?: string
  /**
   * 结束时间
   */
  endTime?: string
}

/**
 * 接口 [手动拉取资金流水↗](http://yapi.zswltec.com:3000/project/11/interface/api/3217) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/manual/pull/flow`
 * @更新时间 `2024-06-11 15:55:29`
 */
export type PullFlowResponse = string[]

/**
 * 接口 [批量核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/3211) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/batch/write/off`
 * @更新时间 `2024-06-13 10:42:08`
 */
export interface WriteOffRequest {
  /**
   * 流水ID列表
   */
  financeFlowIds: number[]
  /**
   * 类型：项目端或者资金端
   */
  sideType?: string
  /**
   * 类型：付款或者收款
   */
  writeOffType?: string
  /**
   * 数据列表json
   */
  listDataJson?: string
}

/**
 * 接口 [批量核销↗](http://yapi.zswltec.com:3000/project/11/interface/api/3211) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/batch/write/off`
 * @更新时间 `2024-06-13 10:42:08`
 */
export interface WriteOffResponse {
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
 * 接口 [改变正在核销的流水展示状态↗](http://yapi.zswltec.com:3000/project/11/interface/api/3535) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/change/showinlist`
 * @更新时间 `2024-05-30 19:15:21`
 */
export interface ChangeShowinlistRequest {
  /**
   * 流水ID列表
   */
  financingFlowIdList: number[]
}

/**
 * 接口 [改变正在核销的流水展示状态↗](http://yapi.zswltec.com:3000/project/11/interface/api/3535) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/change/showinlist`
 * @更新时间 `2024-05-30 19:15:21`
 */
export interface ChangeShowinlistResponse {
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
 * 接口 [无需处理↗](http://yapi.zswltec.com:3000/project/11/interface/api/3223) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/no/processing/require`
 * @更新时间 `2024-05-30 19:15:21`
 */
export interface ProcessingRequireRequest {
  /**
   * 流水的ID列表
   */
  financeFlowIds: number[]
}

/**
 * 接口 [无需处理↗](http://yapi.zswltec.com:3000/project/11/interface/api/3223) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/no/processing/require`
 * @更新时间 `2024-05-30 19:15:21`
 */
export interface ProcessingRequireResponse {
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
 * 接口 [根据借据id和现金流项目查期项↗](http://yapi.zswltec.com:3000/project/11/interface/api/3199) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/phase/list`
 * @更新时间 `2024-05-19 17:02:44`
 */
export interface PhaseListRequest {
  /**
   * 借据ID
   */
  receiptId: number
  /**
   * 现金流项目
   */
  cashFlowItem?: string
}

/**
 * 接口 [根据借据id和现金流项目查期项↗](http://yapi.zswltec.com:3000/project/11/interface/api/3199) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/phase/list`
 * @更新时间 `2024-05-19 17:02:44`
 */
export type PhaseListResponse = {
  /**
   * 现金流ID
   */
  cashFlowId?: number
  /**
   * 期项
   */
  phase?: string
}[]

/**
 * 接口 [根据合同ID和现金流项目查询现金流编号↗](http://yapi.zswltec.com:3000/project/11/interface/api/3739) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/code/list`
 * @更新时间 `2024-06-11 15:57:06`
 */
export interface CodeListRequest {
  /**
   * 合同ID
   */
  contractId: number
  /**
   * 收付款类型 (PAYMENT, COLLECTION)
   */
  writeOffType: string
  /**
   * 现金流项目枚举name (FINANCE_FUND, DEPOSIT_REFUND, PRINCIPAL, DEPOSIT_PAY, FACTORING_FEE, OPEN_LICENSE_FEE, OTHER_FEE, CUSTODY_FEE, GUARANTEE_FEE, FINANCIAL_ADVISORY_FEE, CREDIT_ASSESSMENT_FEE, LOAN_SERVICE_FEE, AUDIT_FEE, INTEREST)
   */
  cashFlowItem: string
}

/**
 * 接口 [根据合同ID和现金流项目查询现金流编号↗](http://yapi.zswltec.com:3000/project/11/interface/api/3739) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/code/list`
 * @更新时间 `2024-06-11 15:57:06`
 */
export type CodeListResponse = string[]

/**
 * 接口 [根据合同ID查询借据编号↗](http://yapi.zswltec.com:3000/project/11/interface/api/3193) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/receipt/list`
 * @更新时间 `2024-05-30 19:15:20`
 */
export interface ReceiptListRequest {
  /**
   * 合同ID
   */
  contractId: number
}

/**
 * 接口 [根据合同ID查询借据编号↗](http://yapi.zswltec.com:3000/project/11/interface/api/3193) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/receipt/list`
 * @更新时间 `2024-05-30 19:15:20`
 */
export type ReceiptListResponse = {
  /**
   * 借据ID
   */
  receiptId?: number
  /**
   * 借据编号
   */
  receiptCode?: string
}[]

/**
 * 接口 [根据现金流ID获取对应子列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3541) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/sub/list`
 * @更新时间 `2024-06-13 10:42:09`
 */
export interface SubListRequest {
  /**
   * 流水ID列表
   */
  financingFlowIdList: number[]
}

/**
 * 接口 [根据现金流ID获取对应子列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3541) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/sub/list`
 * @更新时间 `2024-06-13 10:42:09`
 */
export type SubListResponse = {
  /**
   * 客户ID
   */
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
   * 现金流编号
   */
  cashFlowCode?: string
  /**
   * 现金流项目
   */
  cashFlowItemName?: string
  /**
   * 应收时间/应付时间
   */
  shouldPayTime?: string
  /**
   * 应收金额/应付金额
   */
  shouldPayAmount?: number
  /**
   * 未收金额/未付金额
   */
  noPayAmount?: number
  /**
   * 本次核销金额
   */
  thisWriteOffAmount?: number
  /**
   * 状态
   */
  status?: string
}[]

/**
 * 接口 [根据计划收款ID和期项和现金流项目查询金额↗](http://yapi.zswltec.com:3000/project/11/interface/api/3205) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/amount/detail`
 * @更新时间 `2024-06-13 10:42:08`
 */
export interface AmountDetailRequest {
  /**
   * 现金流编号
   */
  cashFlowCode: string
  /**
   * 合同ID
   */
  contractId: number
  /**
   * 收付款类型 (PAYMENT, COLLECTION)
   */
  writeOffType: string
  /**
   * 现金流项目枚举name (FINANCE_FUND, DEPOSIT_REFUND, PRINCIPAL, DEPOSIT_PAY, FACTORING_FEE, OPEN_LICENSE_FEE, OTHER_FEE, CUSTODY_FEE, GUARANTEE_FEE, FINANCIAL_ADVISORY_FEE, CREDIT_ASSESSMENT_FEE, LOAN_SERVICE_FEE, AUDIT_FEE, INTEREST)
   */
  cashFlowItem: string
}

/**
 * 接口 [根据计划收款ID和期项和现金流项目查询金额↗](http://yapi.zswltec.com:3000/project/11/interface/api/3205) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/project/amount/detail`
 * @更新时间 `2024-06-13 10:42:08`
 */
export interface AmountDetailResponse {
  /**
   * 对应模块记录ID
   */
  modelId?: number
  /**
   * 应付金额
   */
  shouldPayAmount?: number
  /**
   * 应付时间
   */
  shouldPayTime?: string
  /**
   * 未付金额
   */
  noPayAmount?: number
  /**
   * 已核销金额
   */
  writeOffedAmount?: string[]
}

/**
 * 接口 [银行流水中心-各个tab列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3187) 的 **请求类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/list`
 * @更新时间 `2024-06-13 10:42:08`
 */
export interface CenterListRequest {
  /**
   * id
   */
  id?: number
  /**
   * PROCESSING_CENTER, PROCESSED_PROJ_SIDE, PROCESSED_FUNDS_END, NO_PROCESSING_REQUIRE
   */
  tabType: string
  /**
   * 交易明细编号
   */
  transactionDetailsNumber?: string
  /**
   * 对方户名
   */
  otherName?: string
  /**
   * 交易时间开始
   */
  transactionDateFrom?: string
  /**
   * 交易时间结束
   */
  transactionDateTo?: string
  /**
   * 银行账号
   */
  bankAccount?: string
  /**
   * 开户银行
   */
  bankName?: string
  /**
   * 收付款类型
   */
  collectionPaymentType?: string
  /**
   * 对方账号
   */
  otherBankAccount?: string
  /**
   * 对方开户行
   */
  otherBankName?: string
  /**
   * 摘要
   */
  mainInfo?: string
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
 * 接口 [银行流水中心-各个tab列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/3187) 的 **返回类型**
 *
 * @分类 [银行流水中心↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_788)
 * @请求头 `POST /bank/center/list`
 * @更新时间 `2024-06-13 10:42:08`
 */
export interface CenterListResponse {
  /**
   * 查询集合
   */
  list?: {
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
     * 已核销金额
     */
    writeOffedAmountList?: {
      /**
       * 核销金额
       */
      writeOffAmount?: number
      /**
       * 核销时间
       */
      writeOffTime?: string
    }[]
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

/* prettier-ignore-end */
