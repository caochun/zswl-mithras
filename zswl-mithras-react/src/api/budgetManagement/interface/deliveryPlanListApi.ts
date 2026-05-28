/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [预算管理-预算计划-投放计划-信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/33007) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4532)
 * @请求头 `POST /budget/plan/pay/info`
 * @更新时间 `2025-05-09 15:14:18`
 */
export interface PayInfoRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [预算管理-预算计划-投放计划-信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/33007) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4532)
 * @请求头 `POST /budget/plan/pay/info`
 * @更新时间 `2025-05-09 15:14:18`
 */
export interface PayInfoResponse {
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
}

/**
 * 接口 [预算管理-预算计划-投放计划-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32395) 的 **请求类型**
 *
 * @分类 [预算管理-预算计划-投放计划-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4532)
 * @请求头 `POST /budget/plan/pay/pageList`
 * @更新时间 `2025-05-09 15:14:01`
 */
export interface PayPageListRequest {
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
 * 接口 [预算管理-预算计划-投放计划-列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32395) 的 **返回类型**
 *
 * @分类 [预算管理-预算计划-投放计划-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4532)
 * @请求头 `POST /budget/plan/pay/pageList`
 * @更新时间 `2025-05-09 15:14:01`
 */
export interface PayPageListResponse {
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
  others?: {}
}

/* prettier-ignore-end */
