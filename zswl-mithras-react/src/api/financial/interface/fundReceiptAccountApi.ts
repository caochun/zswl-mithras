/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改资金管理-融资管理-对方收款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11761) 的 **请求类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/modify`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 账户名称
   */
  accountName?: string
  /**
   * 银行账号
   */
  accountNum?: string
  /**
   * 开户行
   */
  accountAddress?: string
}

/**
 * 接口 [修改资金管理-融资管理-对方收款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11761) 的 **返回类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/modify`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountModifyResponse {}

/**
 * 接口 [删除资金管理-融资管理-对方收款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11763) 的 **请求类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/remove`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资金管理-融资管理-对方收款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11763) 的 **返回类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/remove`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountRemoveResponse {}

/**
 * 接口 [新增资金管理-融资管理-对方收款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11760) 的 **请求类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/add`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountAddRequest {
  /**
   * 收付款id
   */
  receiptRepayId?: number
  /**
   * 客户名称
   */
  clientName?: string
  /**
   * 账户名称
   */
  accountName?: string
  /**
   * 银行账号
   */
  accountNum?: string
  /**
   * 开户行
   */
  accountAddress?: string
}

/**
 * 接口 [新增资金管理-融资管理-对方收款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11760) 的 **返回类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/add`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountAddResponse {}

/**
 * 接口 [资金管理-融资管理-对方收款账户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11762) 的 **请求类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/list`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountListRequest {
  /**
   * 收付款id
   */
  receiptRepayId: number
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
 * 接口 [资金管理-融资管理-对方收款账户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11762) 的 **返回类型**
 *
 * @分类 [fund-receipt-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2150)
 * @请求头 `POST /fund/receipt/account/list`
 * @更新时间 `2023-02-22 15:30:44`
 */
export interface AccountListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 融资id
     */
    financingId?: number
    /**
     * 客户名称
     */
    clientName?: string
    /**
     * 账户名称
     */
    accountName?: string
    /**
     * 银行账号
     */
    accountNum?: string
    /**
     * 开户行
     */
    accountAddress?: string
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
