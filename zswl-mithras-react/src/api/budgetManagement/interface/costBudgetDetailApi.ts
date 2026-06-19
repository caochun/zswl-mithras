/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改预算管理-预算计划-成本预算-明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/32497) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-成本预算-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4550)
 * @请求头 `POST /budget/plan/cost/detail/modify`
 * @更新时间 `2025-04-16 09:58:19`
 */
export interface DetailModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 成本预算id
   */
  budgetPlanCostId?: number
  /**
   * 投放计划详情id
   */
  budgetPlanPayDetailId?: number
  /**
   * 年份
   */
  year?: number
  /**
   * 月份
   */
  month?: number
  /**
   * 所属部门id
   */
  belongDeptId?: number
  /**
   * 项目评审id
   */
  projReviewId?: number
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 借据id
   */
  receiptId?: number
  /**
   * 存量租金回笼
   */
  projectRentHistory?: number
  /**
   * 存量本金回笼
   */
  projectPrincipalHistory?: number
  /**
   * 存量利息回笼
   */
  projectInterestHistory?: number
  /**
   * 还贷存量
   */
  financeRepayHistory?: number
  /**
   * 归还存量借款本金
   */
  financePrincipalHistory?: number
  /**
   * 归还存量借款利息
   */
  financeInterestHistory?: number
  /**
   * 退回保证金
   */
  projectDepositHistory?: number
  /**
   * 新增保证金
   */
  projectDepositFeature?: number
  /**
   * 新增服务费/咨询费
   */
  projectConsultingFeeFeature?: number
  /**
   * 新增投放
   */
  projectPayFeature?: number
  /**
   * 新增租金回笼
   */
  projectRentFeature?: number
  /**
   * 新增本金回笼
   */
  projectPrincipalFeature?: number
  /**
   * 新增利息回笼
   */
  projectInterestFeature?: number
  /**
   * 资金缺口
   */
  fundGap?: number
}

/**
 * 接口 [修改预算管理-预算计划-成本预算-明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/32497) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-成本预算-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4550)
 * @请求头 `POST /budget/plan/cost/detail/modify`
 * @更新时间 `2025-04-16 09:58:19`
 */
export interface DetailModifyResponse {
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
 * 接口 [删除预算管理-预算计划-成本预算-明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/32491) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-成本预算-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4550)
 * @请求头 `POST /budget/plan/cost/detail/remove`
 * @更新时间 `2025-04-16 09:58:19`
 */
export interface DetailRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除预算管理-预算计划-成本预算-明细↗](http://yapi.zswltec.com:3000/project/11/interface/api/32491) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-成本预算-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4550)
 * @请求头 `POST /budget/plan/cost/detail/remove`
 * @更新时间 `2025-04-16 09:58:19`
 */
export interface DetailRemoveResponse {
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
 * 接口 [预算管理-预算计划-成本预算-明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32503) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-成本预算-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4550)
 * @请求头 `POST /budget/plan/cost/detail/list`
 * @更新时间 `2025-04-16 09:58:19`
 */
export interface DetailListRequest {
  /**
   * 成本预算id
   */
  budgetPlanCostId: number
}

/**
 * 接口 [预算管理-预算计划-成本预算-明细列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32503) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-成本预算-明细-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4550)
 * @请求头 `POST /budget/plan/cost/detail/list`
 * @更新时间 `2025-04-16 09:58:19`
 */
export type DetailListResponse = {
  /**
   * 主键id
   */
  id?: number
  /**
   * 逻辑删除，0-未删除
   */
  deleted?: number
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 成本预算id
   */
  budgetPlanCostId?: number
  /**
   * 投放计划详情id
   */
  budgetPlanPayDetailId?: number
  /**
   * 年份
   */
  year?: number
  /**
   * 月份
   */
  month?: number
  /**
   * 所属部门id
   */
  belongDeptId?: number
  /**
   * 项目评审id
   */
  projReviewId?: number
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 借据id
   */
  receiptId?: number
  /**
   * 存量租金回笼
   */
  projectRentHistory?: number
  /**
   * 存量本金回笼
   */
  projectPrincipalHistory?: number
  /**
   * 存量利息回笼
   */
  projectInterestHistory?: number
  /**
   * 还贷存量
   */
  financeRepayHistory?: number
  /**
   * 归还存量借款本金
   */
  financePrincipalHistory?: number
  /**
   * 归还存量借款利息
   */
  financeInterestHistory?: number
  /**
   * 退回保证金
   */
  projectDepositHistory?: number
  /**
   * 新增保证金
   */
  projectDepositFeature?: number
  /**
   * 新增服务费/咨询费
   */
  projectConsultingFeeFeature?: number
  /**
   * 新增投放
   */
  projectPayFeature?: number
  /**
   * 新增租金回笼
   */
  projectRentFeature?: number
  /**
   * 新增本金回笼
   */
  projectPrincipalFeature?: number
  /**
   * 新增利息回笼
   */
  projectInterestFeature?: number
  /**
   * 资金缺口
   */
  fundGap?: number
}[]

/* prettier-ignore-end */
