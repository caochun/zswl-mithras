/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [保证金明细列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11744) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-cash-deposit-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2140)
 * @请求头 `POST /fund/receipt/repay/cash/deposit/list`
 * @更新时间 `2023-02-22 16:56:42`
 */
export interface DepositListRequest {
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
 * 接口 [保证金明细列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11744) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-cash-deposit-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2140)
 * @请求头 `POST /fund/receipt/repay/cash/deposit/list`
 * @更新时间 `2023-02-22 16:56:42`
 */
export interface DepositListResponse {
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
    amount?: number
    /**
     * 核销状态
     */
    writeOffState?: string
    /**
     * 本月支付金额
     */
    paidAmount?: number
    /**
     * 本月收入金额
     */
    receiptAmount?: number
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

/**
 * 接口 [修改保证金明细↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11743) 的 **请求类型**
 *
 * @分类 [fund-receipt-repay-cash-deposit-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2140)
 * @请求头 `POST /fund/receipt/repay/cash/deposit/modify`
 * @更新时间 `2023-02-22 16:56:42`
 */
export type DepositModifyRequest = {
  /**
   * 主键
   */
  id?: number
  /**
   * 本月支付金额
   */
  paidAmount?: number
  /**
   * 本月收入金额
   */
  receiptAmount?: number
  /**
   * 备注
   */
  remark?: string
}[]

/**
 * 接口 [修改保证金明细↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11743) 的 **返回类型**
 *
 * @分类 [fund-receipt-repay-cash-deposit-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2140)
 * @请求头 `POST /fund/receipt/repay/cash/deposit/modify`
 * @更新时间 `2023-02-22 16:56:42`
 */
export interface DepositModifyResponse {}

/* prettier-ignore-end */
