/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改预算管理-预算计划-成本预算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32479) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-成本预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4544)
 * @请求头 `POST /budget/plan/cost/modify`
 * @更新时间 `2025-04-16 09:56:53`
 */
export interface CostModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 计划名称
   */
  budgetPlanName?: string
  /**
   * 预算计划开始日期
   */
  budgetDateFrom?: string
  /**
   * 预算计划结束日期
   */
  budgetDateTo?: string
  /**
   * 计划填报开始日期
   */
  writeDateFrom?: string
  /**
   * 计划填报结束日期
   */
  writeDateTo?: string
  /**
   * 预算类型
   */
  budgetType?: string
  /**
   * 状态
   */
  budgetStatus?: string
  /**
   * 收集截止日期
   */
  collectDateTo?: string
}

/**
 * 接口 [修改预算管理-预算计划-成本预算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32479) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-成本预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4544)
 * @请求头 `POST /budget/plan/cost/modify`
 * @更新时间 `2025-04-16 09:56:53`
 */
export interface CostModifyResponse {
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
 * 接口 [删除预算管理-预算计划-成本预算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32473) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-成本预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4544)
 * @请求头 `POST /budget/plan/cost/remove`
 * @更新时间 `2025-04-16 09:56:53`
 */
export interface CostRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除预算管理-预算计划-成本预算↗](http://yapi.zswltec.com:3000/project/11/interface/api/32473) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-成本预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4544)
 * @请求头 `POST /budget/plan/cost/remove`
 * @更新时间 `2025-04-16 09:56:53`
 */
export interface CostRemoveResponse {
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
 * 接口 [预算管理-预算计划-成本预算列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32485) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-成本预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4544)
 * @请求头 `POST /budget/plan/cost/list`
 * @更新时间 `2025-04-16 09:56:53`
 */
export interface CostListRequest {
  /**
   * 状态 budgetStatusEnum
   */
  budgetStatus?: string
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
 * 接口 [预算管理-预算计划-成本预算列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32485) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-成本预算-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4544)
 * @请求头 `POST /budget/plan/cost/list`
 * @更新时间 `2025-04-16 09:56:53`
 */
export interface CostListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 预算计划id
     */
    budgetPlanId?: number
    /**
     * 计划名称
     */
    budgetPlanName?: string
    /**
     * 预算计划开始日期
     */
    budgetDateFrom?: string
    /**
     * 预算计划结束日期
     */
    budgetDateTo?: string
    /**
     * 计划填报开始日期
     */
    writeDateFrom?: string
    /**
     * 计划填报结束日期
     */
    writeDateTo?: string
    /**
     * 预算类型
     */
    budgetType?: string
    /**
     * 状态
     */
    budgetStatus?: string
    /**
     * 收集截止日期
     */
    collectDateTo?: string
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

/* prettier-ignore-end */
