/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [保存我方账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/7032) 的 **请求类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/save`
 * @更新时间 `2023-02-22 11:25:37`
 */
export interface BankaccountSaveRequest {
  /**
   * id
   */
  id?: number
  /**
   * 账户类型
   */
  accountType: string
  /**
   * 账户名称
   */
  accountName: string
  /**
   * 银行账号
   */
  accountNumber: string
  /**
   * 开户银行
   */
  accountBank: string
  /**
   * 是否贷款账户 0-否 1-是
   */
  isLoan: number
  /**
   * 账户状态
   */
  accountStatus: string
  /**
   * 开户时间 yyyy-MM-dd
   */
  openingDate?: string
  /**
   * 币种
   */
  currency?: string
  /**
   * 账户余额
   */
  accountBalance?: number
  /**
   * 备注
   */
  remark?: string
}

/**
 * 接口 [保存我方账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/7032) 的 **返回类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/save`
 * @更新时间 `2023-02-22 11:25:37`
 */
export type BankaccountSaveResponse = number

/**
 * 接口 [删除我方账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/7036) 的 **请求类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/delete`
 * @更新时间 `2023-02-22 11:25:36`
 */
export interface BankaccountDeleteRequest {
  /**
   * 业务数据主键id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [删除我方账户↗](http://api-dev.zswl.cn:3011/project/11/interface/api/7036) 的 **返回类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/delete`
 * @更新时间 `2023-02-22 11:25:36`
 */
export interface BankaccountDeleteResponse {
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
 * 接口 [我方账户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/7040) 的 **请求类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/list`
 * @更新时间 `2023-02-22 11:25:37`
 */
export interface BankaccountListRequest {
  /**
   * 银行名称
   */
  bankName?: string
  /**
   * 账户余额（起）
   */
  accountBalanceFrom?: number
  /**
   * 账户余额（止）
   */
  accountBalanceTo?: number
  /**
   * 是否贷款账户
   */
  isLoan?: number
  /**
   * 创建时间（起） yyyy-MM-dd
   */
  createTimeFrom?: string
  /**
   * 创建时间（止） yyyy-MM-dd
   */
  createTimeTo?: string
  /**
   * 修改时间（起） yyyy-MM-dd
   */
  updateTimeFrom?: string
  /**
   * 修改时间（止） yyyy-MM-dd
   */
  updateTimeTo?: string
}

/**
 * 接口 [我方账户列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/7040) 的 **返回类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/list`
 * @更新时间 `2023-02-22 11:25:37`
 */
export type BankaccountListResponse = {
  /**
   * 账户id
   */
  id?: number
  /**
   * 账户名称
   */
  accountName?: string
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 银行账号
   */
  accountNumber?: string
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 是否贷款账户 0-否 1-是
   */
  isLoan?: number
  /**
   * 账户状态
   */
  accountStatus?: string
  /**
   * 开户时间 yyyy-MM-dd
   */
  openingDate?: string
  /**
   * 币种
   */
  currency?: string
  /**
   * 账户余额
   */
  accountBalance?: number
  /**
   * 创建人id
   */
  createUserId?: number
  /**
   * 创建人名称
   */
  createUserName?: string
  /**
   * 创建时间
   */
  createTime?: string
  /**
   * 修改时间
   */
  updateTime?: string
}[]

/**
 * 接口 [我方账户详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11758) 的 **请求类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/detail`
 * @更新时间 `2023-02-22 11:25:36`
 */
export interface BankaccountDetailRequest {
  /**
   * 业务数据主键id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [我方账户详情↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11758) 的 **返回类型**
 *
 * @分类 [基础数据-我方账户相关接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_1338)
 * @请求头 `POST /basedata/bankaccount/detail`
 * @更新时间 `2023-02-22 11:25:36`
 */
export interface BankaccountDetailResponse {
  /**
   * 账户id
   */
  id?: number
  /**
   * 账户名称
   */
  accountName?: string
  /**
   * 账户类型
   */
  accountType?: string
  /**
   * 账号
   */
  accountNumber?: string
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 是否贷款账户 0-否 1-是
   */
  isLoan?: number
  /**
   * 账户状态
   */
  accountStatus?: string
  /**
   * 开户时间 yyyy-MM-dd
   */
  openingDate?: string
  /**
   * 币种
   */
  currency?: string
  /**
   * 账户余额
   */
  accountBalance?: number
  /**
   * 备注
   */
  remark?: string
}

/* prettier-ignore-end */
