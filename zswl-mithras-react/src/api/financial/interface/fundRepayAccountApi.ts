/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改资金管理-融资管理-我方付款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11765) 的 **请求类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/modify`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 融资id
   */
  financingId?: number
  /**
   * 基础数据-我方银行账户id
   */
  bankAccountId?: number
  /**
   * 支行名称
   */
  accountBank?: string
  /**
   * 账号
   */
  accountNumber?: string
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 开户时间
   */
  accountOpeningDate?: string
}

/**
 * 接口 [修改资金管理-融资管理-我方付款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11765) 的 **返回类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/modify`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountModifyResponse {}

/**
 * 接口 [删除资金管理-融资管理-我方付款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11767) 的 **请求类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/remove`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountRemoveRequest {
  /**
   * id
   */
  id: number
}

/**
 * 接口 [删除资金管理-融资管理-我方付款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11767) 的 **返回类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/remove`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountRemoveResponse {}

/**
 * 接口 [新增资金管理-融资管理-我方付款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11764) 的 **请求类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/add`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountAddRequest {
  /**
   * 融资id
   */
  financingId?: number
  /**
   * 基础数据-我方银行账户id
   */
  bankAccountId?: number
  /**
   * 支行名称
   */
  accountBank?: string
  /**
   * 账号
   */
  accountNumber?: string
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 开户时间
   */
  accountOpeningDate?: string
}

/**
 * 接口 [新增资金管理-融资管理-我方付款账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11764) 的 **返回类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/add`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountAddResponse {}

/**
 * 接口 [资金管理-融资管理-我方付款账户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11766) 的 **请求类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/list`
 * @更新时间 `2023-02-22 15:30:48`
 */
export interface AccountListRequest {
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
 * 接口 [资金管理-融资管理-我方付款账户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11766) 的 **返回类型**
 *
 * @分类 [fund-repay-account-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2152)
 * @请求头 `POST /fund/repay/account/list`
 * @更新时间 `2023-02-22 15:30:48`
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
     * 基础数据-我方银行账户id
     */
    bankAccountId?: number
    /**
     * 支行名称
     */
    accountBank?: string
    /**
     * 账号
     */
    accountNumber?: string
    /**
     * 账户类型
     */
    accountType?: string
    /**
     * 开户时间
     */
    accountOpeningDate?: string
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
