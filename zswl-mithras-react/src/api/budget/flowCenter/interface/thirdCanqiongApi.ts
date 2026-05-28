/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [付款记录明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/9908) 的 **请求类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/payment/record`
 * @更新时间 `2022-10-19 19:41:45`
 */
export interface PaymentRecordRequest {
  /**
   * 付款方式
   */
  paymentMethod?: string
  /**
   * 实付金额
   */
  paidInAmount?: number
  /**
   * 流水Id
   */
  flowId?: string
  /**
   * 实付日期
   */
  paidInDate?: string
  /**
   * 我方账户名
   */
  ourAccountName?: string
  /**
   * 我方账号
   */
  ourAccountNumber?: string
  /**
   * 我方开户行
   */
  ourAccountBank?: string
  /**
   * 对方账户名
   */
  oppositeAccountName?: string
  /**
   * 收款人银行账号
   */
  oppositeAccountNumber?: string
  /**
   * 收款人开户行
   */
  oppositeAccountBank?: string
  /**
   * 现金流项目
   */
  cashFlowItem?: string
  /**
   * 付款编号
   */
  collectionCode?: string
  /**
   * 私钥+时间戳+随机数 的sha1加密结果
   */
  secret?: string
  /**
   * 随机数
   */
  random?: string
  /**
   * 时间戳
   */
  timestamp?: number
}

/**
 * 接口 [付款记录明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/9908) 的 **返回类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/payment/record`
 * @更新时间 `2022-10-19 19:41:45`
 */
export interface PaymentRecordResponse {}

/**
 * 接口 [保证金管理-内扣\/退回↗](http://yapi.zswltec.com:3000/project/11/interface/api/9916) 的 **请求类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/margin/record`
 * @更新时间 `2022-10-19 19:43:02`
 */
export interface MarginRecordRequest {
  /**
   * 信息来源
   */
  dataSource?: number
  /**
   * 保证金处理类型，REFUND_MARGIN(保证金退款), REFUND_MARGIN_DEDUCT(保证金抵扣)
   */
  collectionType: string
  /**
   * 实退日期
   */
  collectionDate: string
  /**
   * 实退金额
   */
  collectionAmount: number
  /**
   * 我方账户名
   */
  ourAccountName?: string
  /**
   * 我方银行账号
   */
  ourAccountNumber?: string
  /**
   * 我方开户行
   */
  ourAccountBank?: string
  /**
   * 对方账户名
   */
  oppositeAccountName?: string
  /**
   * 收款人银行账号
   */
  oppositeAccountNumber?: string
  /**
   * 收款人开户行
   */
  oppositeAccountBank?: string
  /**
   * 私钥+时间戳+随机数 的sha1加密结果
   */
  secret?: string
  /**
   * 随机数
   */
  random?: string
  /**
   * 时间戳
   */
  timestamp?: number
}

/**
 * 接口 [保证金管理-内扣\/退回↗](http://yapi.zswltec.com:3000/project/11/interface/api/9916) 的 **返回类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/margin/record`
 * @更新时间 `2022-10-19 19:43:02`
 */
export interface MarginRecordResponse {}

/**
 * 接口 [收款记录明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/9912) 的 **请求类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/collection/record`
 * @更新时间 `2022-10-19 19:42:40`
 */
export interface CollectionRecordRequest {
  /**
   * 信息来源
   */
  dataSource?: number
  /**
   * 收款编号
   */
  collectionCode: number
  /**
   * 收款类型
   */
  collectionType: string
  /**
   * 实收日期
   */
  collectionDate: string
  /**
   * 实收金额
   */
  collectionAmount: number
  /**
   * 现金流项目
   */
  cashFlowItem: string
  /**
   * 是否开票 0不开 1开
   */
  invoiceFlag?: number
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
   * 我方账户名
   */
  ourAccountName?: string
  /**
   * 我方银行账号
   */
  ourAccountNumber?: string
  /**
   * 我方开户行
   */
  ourAccountBank?: string
  /**
   * 流水ID
   */
  pknumber?: string
  /**
   * 私钥+时间戳+随机数 的sha1加密结果
   */
  secret?: string
  /**
   * 随机数
   */
  random?: string
  /**
   * 时间戳
   */
  timestamp?: number
}

/**
 * 接口 [收款记录明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/9912) 的 **返回类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/collection/record`
 * @更新时间 `2022-10-19 19:42:40`
 */
export interface CollectionRecordResponse {}

/**
 * 接口 [苍穹接口撤回↗](http://yapi.zswltec.com:3000/project/11/interface/api/6481) 的 **请求类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/financial/withdraw`
 * @更新时间 `2024-08-01 16:40:02`
 */
export type FinancialWithdrawRequest = {
  /**
   * 来源途径 ExceptionSourceENUM
   */
  source?: string
  /**
   * 流水类型 cqBillTypeEnum
   */
  platform?: string
  /**
   * 业务主建 用于寻找对应业务信息
   */
  businessKey?: string
}[]

/**
 * 接口 [苍穹接口撤回↗](http://yapi.zswltec.com:3000/project/11/interface/api/6481) 的 **返回类型**
 *
 * @分类 [三方-财务系统-苍穹↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1770)
 * @请求头 `POST /third/financial/withdraw`
 * @更新时间 `2024-08-01 16:40:02`
 */
export interface FinancialWithdrawResponse {
  state?: string
  success?: boolean
  message?: string
  status?: boolean
  data?: string
}

/* prettier-ignore-end */
