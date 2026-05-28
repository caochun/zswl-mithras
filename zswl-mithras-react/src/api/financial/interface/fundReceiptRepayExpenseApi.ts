/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改费用一览表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11747) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-expense-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2144)
 * @请求头 `POST /fund/receipt/repay/expense/modify`
 * @更新时间 `2023-02-22 16:56:33`
 */
export type ExpenseModifyRequest = {
  /**
   * 主键
   */
  id?: number
  /**
   * 本月支付金额
   */
  payAmount?: number
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
 * 接口 [修改费用一览表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11747) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-expense-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2144)
 * @请求头 `POST /fund/receipt/repay/expense/modify`
 * @更新时间 `2023-02-22 16:56:33`
 */
export interface ExpenseModifyResponse {}

/**
 * 接口 [费用一览表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11748) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-expense-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2144)
 * @请求头 `POST /fund/receipt/repay/expense/list`
 * @更新时间 `2023-02-22 16:56:34`
 */
export interface ExpenseListRequest {
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
 * 接口 [费用一览表列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11748) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-expense-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2144)
 * @请求头 `POST /fund/receipt/repay/expense/list`
 * @更新时间 `2023-02-22 16:56:34`
 */
export interface ExpenseListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 收付款id
     */
    receiptRepayId?: number
    /**
     * 费用类型
     */
    expenseType?: string
    /**
     * 金额
     */
    totalAmount?: number
    /**
     * 累计已支付金额
     */
    totalPaidAmount?: number
    /**
     * 本月支付金额
     */
    payAmount?: number
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
