/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改借款流入↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11741) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-borrowing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2138)
 * @请求头 `POST /fund/receipt/repay/borrowing/modify`
 * @更新时间 `2023-02-22 16:56:50`
 */
export type BorrowingModifyRequest = {
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
 * 接口 [修改借款流入↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11741) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-borrowing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2138)
 * @请求头 `POST /fund/receipt/repay/borrowing/modify`
 * @更新时间 `2023-02-22 16:56:50`
 */
export interface BorrowingModifyResponse {}

/**
 * 接口 [借款流入列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11742) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-borrowing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2138)
 * @请求头 `POST /fund/receipt/repay/borrowing/list`
 * @更新时间 `2023-02-22 16:56:50`
 */
export interface BorrowingListRequest {
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
 * 接口 [借款流入列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11742) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-borrowing-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2138)
 * @请求头 `POST /fund/receipt/repay/borrowing/list`
 * @更新时间 `2023-02-22 16:56:50`
 */
export interface BorrowingListResponse {
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
    cashFlowCode?: string
    /**
     * 期项
     */
    term?: number
    /**
     * 本金
     */
    principal?: number
    /**
     * 核销状态
     */
    writeOffState?: string
    /**
     * 实际贷款日期
     */
    actualLoanDate?: string
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
