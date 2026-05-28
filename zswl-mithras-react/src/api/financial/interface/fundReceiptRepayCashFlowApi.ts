/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改本金利息一览表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11745) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-cash-flow-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2142)
 * @请求头 `POST /fund/receipt/repay/cash/flow/modify`
 * @更新时间 `2023-02-22 16:56:26`
 */
export type FlowModifyRequest = {
  /**
   * 主键
   */
  id?: number
  /**
   * 备注
   */
  remark?: string
}[]

/**
 * 接口 [修改本金利息一览表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11745) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-cash-flow-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2142)
 * @请求头 `POST /fund/receipt/repay/cash/flow/modify`
 * @更新时间 `2023-02-22 16:56:26`
 */
export interface FlowModifyResponse {}

/**
 * 接口 [本金利息一览表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11746) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-cash-flow-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2142)
 * @请求头 `POST /fund/receipt/repay/cash/flow/list`
 * @更新时间 `2023-02-22 16:56:27`
 */
export interface FlowListRequest {
  /**
   * 收付款id
   */
  receiptRepayId?: number
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
}

/**
 * 接口 [本金利息一览表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11746) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-cash-flow-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2142)
 * @请求头 `POST /fund/receipt/repay/cash/flow/list`
 * @更新时间 `2023-02-22 16:56:27`
 */
export interface FlowListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 现金流编号
     */
    cashFlowNo?: string
    /**
     * 期项
     */
    term?: string
    /**
     * 计划还款日
     */
    planRepayDate?: string
    /**
     * 本金
     */
    principal?: number
    /**
     * 利息
     */
    interest?: number
    /**
     * 本月支付本金
     */
    planRepayPrincipal?: number
    /**
     * 本月支付利息
     */
    planRepayInterest?: number
    /**
     * 核销状态
     */
    writeOffState?: string
    /**
     * 备注
     */
    remark?: string
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
}

/* prettier-ignore-end */
