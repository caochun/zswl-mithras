/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改合同-提前还款表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6420) 的 **请求类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/modify`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 主合同id
   */
  contractId?: number
  /**
   * 是否提前结清，0-否，1-是
   */
  isEarlySettle: number
  /**
   * 保证金是否抵扣，0-否，1-是
   */
  isEarnestMoneyDeduction?: number
  /**
   * 保证金抵扣金额
   */
  earnestMoneyDeductionAmount?: number
  /**
   * 变更类型;CHANGE_INTEREST（调息）、EARLY_REPAYMENT（提前还款）、EXTENSION（展期）、OTHER（其他）
   */
  changeType?: string
  /**
   * 提前还款日期
   */
  applayRepaymentDate: string
  /**
   * 到期未付租金
   */
  unpaidRentDue: number
  /**
   * 违约金
   */
  penalty?: number
  /**
   * 违约金减免方式
   */
  penaltyDerateType: string
  /**
   * 违约金减免百分比
   */
  penaltyDeratePercent?: number
  /**
   * 违约金减免金额
   */
  penaltyDerateAmount?: number
  /**
   * 提前归还本金
   */
  earlyRepayment: number
  /**
   * 提前归还利息
   */
  earlyRepaymentInterest?: number
  /**
   * 提前终止补偿金
   */
  loss?: number
  /**
   * 提前终止补偿金减免方式
   */
  lossDerateType: string
  /**
   * 提前终止补偿金减免百分比
   */
  lossDeratePercent?: number
  /**
   * 提前终止补偿金减免金额
   */
  applyDerateAmount: number
  /**
   * 到期未付租金
   */
  unpaidRent?: number
  /**
   * 说明
   */
  remark?: string
  /**
   * 名义价款
   */
  nominalPrice?: number
}

/**
 * 接口 [修改合同-提前还款表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6420) 的 **返回类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/modify`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentModifyResponse {
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
 * 接口 [合同-提前还款表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6424) 的 **请求类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/list`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentListRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [合同-提前还款表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6424) 的 **返回类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/list`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentListResponse {
  /**
   * id
   */
  id?: number
  /**
   * 主合同id
   */
  contractId?: number
  /**
   * 是否提前结清，0-否，1-是
   */
  isEarlySettle?: number
  /**
   * 保证金是否抵扣，0-否，1-是
   */
  isEarnestMoneyDeduction?: number
  /**
   * 保证金抵扣金额
   */
  earnestMoneyDeductionAmount?: number
  /**
   * 申请还款日期
   */
  applayRepaymentDate?: string
  /**
   * 到期未付租金
   */
  unpaidRentDue?: number
  /**
   * 违约金
   */
  penalty?: number
  /**
   * 违约金减免方式
   */
  penaltyDerateType?: string
  /**
   * 违约金减免百分比
   */
  penaltyDeratePercent?: number
  /**
   * 违约金减免金额
   */
  penaltyDerateAmount: number
  /**
   * 提前归还本金
   */
  earlyRepayment?: number
  /**
   * 提前归还利息
   */
  earlyRepaymentInterest?: number
  /**
   * 提前终止补偿金
   */
  loss?: number
  /**
   * 提前终止补偿金减免方式
   */
  lossDerateType?: string
  /**
   * 提前终止补偿金减免百分比
   */
  lossDeratePercent?: number
  /**
   * 提前终止补偿金减免金额
   */
  applyDerateAmount?: number
  /**
   * 名义价款
   */
  nominalPrice?: number
  /**
   * 名义价款申请还款日期（字段废弃）
   */
  nominalPriceDate?: string
  /**
   * 说明
   */
  remark?: string
}

/**
 * 接口 [提前还款表-计算↗](http://yapi.zswltec.com:3000/project/11/interface/api/7028) 的 **请求类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/calculation`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentCalculationRequest {
  /**
   * 是否提前结清，0-否，1-是
   */
  isEarlySettle: number
  /**
   * 保证金是否抵扣，0-否，1-是
   */
  isEarnestMoneyDeduction?: number
  /**
   * 保证金抵扣金额
   */
  earnestMoneyDeductionAmount?: number
  /**
   * 提前还款日期
   */
  applayRepaymentDate?: string
  /**
   * 到期未付租金
   */
  unpaidRentDue?: number
  /**
   * 违约金
   */
  penalty?: number
  /**
   * 违约金减免方式
   */
  penaltyDerateType: string
  /**
   * 违约金减免百分比
   */
  penaltyDeratePercent?: number
  /**
   * 违约金减免金额
   */
  penaltyDerateAmount?: number
  /**
   * 提前归还本金
   */
  earlyRepayment?: number
  /**
   * 提前归还利息
   */
  earlyRepaymentInterest?: number
  /**
   * 提前终止补偿金
   */
  loss?: number
  /**
   * 提前终止补偿金减免方式
   */
  lossDerateType: string
  /**
   * 提前终止补偿金减免百分比
   */
  lossDeratePercent?: number
  /**
   * 提前终止补偿金减免金额
   */
  applyDerateAmount: number
  /**
   * 名义价款
   */
  nominalPrice?: number
  /**
   * 说明
   */
  remark?: string
  /**
   * 主合同id
   */
  contractId: number
}

/**
 * 接口 [提前还款表-计算↗](http://yapi.zswltec.com:3000/project/11/interface/api/7028) 的 **返回类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/calculation`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentCalculationResponse {
  /**
   * 是否提前结清，0-否，1-是
   */
  isEarlySettle: number
  /**
   * 保证金是否抵扣，0-否，1-是
   */
  isEarnestMoneyDeduction?: number
  /**
   * 保证金抵扣金额
   */
  earnestMoneyDeductionAmount?: number
  /**
   * 提前还款日期
   */
  applayRepaymentDate?: string
  /**
   * 到期未付租金
   */
  unpaidRentDue?: number
  /**
   * 违约金
   */
  penalty?: number
  /**
   * 违约金减免方式
   */
  penaltyDerateType: string
  /**
   * 违约金减免百分比
   */
  penaltyDeratePercent?: number
  /**
   * 违约金减免金额
   */
  penaltyDerateAmount?: number
  /**
   * 提前归还本金
   */
  earlyRepayment?: number
  /**
   * 提前归还利息
   */
  earlyRepaymentInterest?: number
  /**
   * 提前终止补偿金
   */
  loss?: number
  /**
   * 提前终止补偿金减免方式
   */
  lossDerateType: string
  /**
   * 提前终止补偿金减免百分比
   */
  lossDeratePercent?: number
  /**
   * 提前终止补偿金减免金额
   */
  applyDerateAmount: number
  /**
   * 名义价款
   */
  nominalPrice?: number
  /**
   * 说明
   */
  remark?: string
  /**
   * 主合同id
   */
  contractId: number
}

/**
 * 接口 [新增合同-提前还款表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6416) 的 **请求类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/add`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentAddRequest {
  /**
   * 是否提前结清，0-否，1-是
   */
  isEarlySettle: number
  /**
   * 保证金是否抵扣，0-否，1-是
   */
  isEarnestMoneyDeduction?: number
  /**
   * 保证金抵扣金额
   */
  earnestMoneyDeductionAmount?: number
  /**
   * 提前还款日期
   */
  applayRepaymentDate?: string
  /**
   * 到期未付租金
   */
  unpaidRentDue?: number
  /**
   * 违约金
   */
  penalty?: number
  /**
   * 违约金减免方式
   */
  penaltyDerateType: string
  /**
   * 违约金减免百分比
   */
  penaltyDeratePercent?: number
  /**
   * 违约金减免金额
   */
  penaltyDerateAmount?: number
  /**
   * 提前归还本金
   */
  earlyRepayment?: number
  /**
   * 提前归还利息
   */
  earlyRepaymentInterest?: number
  /**
   * 提前终止补偿金
   */
  loss?: number
  /**
   * 提前终止补偿金减免方式
   */
  lossDerateType: string
  /**
   * 提前终止补偿金减免百分比
   */
  lossDeratePercent?: number
  /**
   * 提前终止补偿金减免金额
   */
  applyDerateAmount: number
  /**
   * 名义价款
   */
  nominalPrice?: number
  /**
   * 说明
   */
  remark?: string
  /**
   * 主合同id
   */
  contractId: number
}

/**
 * 接口 [新增合同-提前还款表↗](http://yapi.zswltec.com:3000/project/11/interface/api/6416) 的 **返回类型**
 *
 * @分类 [合同-提前还款表表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_1226)
 * @请求头 `POST /contract/prepayment/add`
 * @更新时间 `2024-12-05 15:45:17`
 */
export interface ContractprepaymentAddResponse {
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
