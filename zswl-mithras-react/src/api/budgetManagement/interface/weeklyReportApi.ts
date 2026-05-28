/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [预算管理-投放计划-项目周报列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32803) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划-项目周报-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4622)
 * @请求头 `POST /budget/plan/pay/weekly/report/list`
 * @更新时间 `2025-05-15 16:23:09`
 */
export interface ReportListRequest {
  /**
   * 状态
   */
  planStatus?: string
  /**
   * 预算计划名称
   */
  budgetPlanName?: string
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
 * 接口 [预算管理-投放计划-项目周报列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/32803) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划-项目周报-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4622)
 * @请求头 `POST /budget/plan/pay/weekly/report/list`
 * @更新时间 `2025-05-15 16:23:09`
 */
export interface ReportListResponse {
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
     * 预算计划名称
     */
    budgetPlanName?: string
    /**
     * 投放计划id
     */
    budgetPlanPayId?: number
    /**
     * 周报区间-起
     */
    dateFrom?: string
    /**
     * 周报区间-止
     */
    dateTo?: string
    /**
     * 状态
     */
    planStatus?: string
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
 * 接口 [预算管理-预算计划-投放计划-信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/33979) 的 **请求类型**
 *
 * @分类 [预算管理-投放计划-项目周报-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4622)
 * @请求头 `POST /budget/plan/pay/weekly/report/info`
 * @更新时间 `2025-05-15 16:23:10`
 */
export interface ReportInfoRequest {
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
 * 接口 [预算管理-预算计划-投放计划-信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/33979) 的 **返回类型**
 *
 * @分类 [预算管理-投放计划-项目周报-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_4622)
 * @请求头 `POST /budget/plan/pay/weekly/report/info`
 * @更新时间 `2025-05-15 16:23:10`
 */
export interface ReportInfoResponse {
  /**
   * 主键id
   */
  id?: number
  /**
   * 预算计划id
   */
  budgetPlanId?: number
  /**
   * 预算计划名称
   */
  budgetPlanName?: string
  /**
   * 投放计划id
   */
  budgetPlanPayId?: number
  /**
   * 周报区间-起
   */
  dateFrom?: string
  /**
   * 周报区间-止
   */
  dateTo?: string
  /**
   * 状态
   */
  planStatus?: string
}

/* prettier-ignore-end */
