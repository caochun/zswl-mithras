/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改账户↗](http://yapi.zswltec.com:3000/project/11/interface/api/11780) 的 **请求类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/modify`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 融资id
   */
  financingId?: number
  /**
   * 银行名称
   */
  accountBank?: string
  /**
   * 基础数据-我方银行账户id
   */
  bankAccountId?: number
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 账号
   */
  accountNumber?: string
  /**
   * 开户时间
   */
  accountOpeningDate?: string
}

/**
 * 接口 [修改账户↗](http://yapi.zswltec.com:3000/project/11/interface/api/11780) 的 **返回类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/modify`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountModifyResponse {
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
 * 接口 [创建账户↗](http://yapi.zswltec.com:3000/project/11/interface/api/11778) 的 **请求类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/create`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountCreateRequest {
  /**
   * 融资id
   */
  financingId: number
  /**
   * 银行名称
   */
  accountBank: string
  /**
   * 基础数据-我方银行账户id
   */
  bankAccountId: number
  /**
   * 账户类型
   */
  accountType: string
  /**
   * 账号
   */
  accountNumber: string
  /**
   * 开户时间
   */
  accountOpeningDate?: string
}

/**
 * 接口 [创建账户↗](http://yapi.zswltec.com:3000/project/11/interface/api/11778) 的 **返回类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/create`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountCreateResponse {
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
 * 接口 [删除账户↗](http://yapi.zswltec.com:3000/project/11/interface/api/11781) 的 **请求类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/delete`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountDeleteRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [删除账户↗](http://yapi.zswltec.com:3000/project/11/interface/api/11781) 的 **返回类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/delete`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountDeleteResponse {
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
 * 接口 [查询银行名称↗](http://yapi.zswltec.com:3000/project/11/interface/api/11776) 的 **请求类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/bank`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountBankRequest {
  /**
   * 银行名称
   */
  accountBank?: string
  /**
   * 账户
   */
  accountNumber?: string
}

/**
 * 接口 [查询银行名称↗](http://yapi.zswltec.com:3000/project/11/interface/api/11776) 的 **返回类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/bank`
 * @更新时间 `2024-10-14 19:12:38`
 */
export type AccountBankResponse = {
  /**
   * 银行名称
   */
  accountBank?: string
  /**
   * 账户
   */
  accountNumber?: string
}[]

/**
 * 接口 [查询银行账号等信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/11779) 的 **请求类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/bank/info`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface BankInfoRequest {
  /**
   * 银行名称
   */
  accountBank?: string
  /**
   * 账户
   */
  accountNumber?: string
}

/**
 * 接口 [查询银行账号等信息↗](http://yapi.zswltec.com:3000/project/11/interface/api/11779) 的 **返回类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/bank/info`
 * @更新时间 `2024-10-14 19:12:38`
 */
export type BankInfoResponse = {
  /**
   * 基础数据-我方银行账户id
   */
  bankAccountId?: number
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 账户名称
   */
  accountName?: string
  /**
   * 账号
   */
  accountNumber?: string
  /**
   * 支行名称
   */
  accountBank?: string
  /**
   * 开户时间
   */
  accountOpeningDate?: string
}[]

/**
 * 接口 [获取账户列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/11777) 的 **请求类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/list`
 * @更新时间 `2024-10-14 19:12:38`
 */
export interface AccountListRequest {
  /**
   * 融资id
   */
  financingId?: number
  /**
   * 版本号
   */
  version?: string
  sourceScene?: string
}

/**
 * 接口 [获取账户列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/11777) 的 **返回类型**
 *
 * @分类 [融资管理-我方还款账户相关接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_2158)
 * @请求头 `POST /fund/financing/pay/account/list`
 * @更新时间 `2024-10-14 19:12:38`
 */
export type AccountListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 基础数据-我方银行账户id
   */
  bankAccountId?: number
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 账号
   */
  accountNumber?: string
  /**
   * 支行名称
   */
  accountBank?: string
  /**
   * 开户时间
   */
  accountOpeningDate?: string
}[]

/* prettier-ignore-end */
