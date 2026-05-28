/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [监管户待转资金列表展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26905) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/account/list`
 * @更新时间 `2025-01-03 09:42:06`
 */
export interface FundTransferaccountListRequest {
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 起止日期-开始
   */
  queryDateStart: string
  /**
   * 起止日期-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [监管户待转资金列表展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26905) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/account/list`
 * @更新时间 `2025-01-03 09:42:06`
 */
export interface FundTransferaccountListResponse {
  /**
   * 列表-以账户和日期为维度
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 数据时点
     */
    date?: string
    /**
     * 账户基本表id
     */
    accountId?: number
    /**
     * 开户银行
     */
    accountBank?: string
    /**
     * 银行账号
     */
    accountNumber?: string
    /**
     * 账户性质
     */
    accountType?: string
    /**
     * 结余-预估
     */
    estimateBalanceAmount?: number
    /**
     * 结余受限-预估
     */
    estimateBalanceLimitAmount?: number
    /**
     * 结余-实际
     */
    actualBalanceAmount?: number
    /**
     * 待转余额
     */
    pendingBalanceAmount?: number
  }[]
  /**
   * 合计-以日期为维度
   */
  sum?: {
    /**
     * 数据时点
     */
    date?: string
    /**
     * 总合计
     */
    allSum?: {
      /**
       * id
       */
      id?: number
      /**
       * 数据时点
       */
      date?: string
      /**
       * 账户基本表id
       */
      accountId?: number
      /**
       * 开户银行
       */
      accountBank?: string
      /**
       * 银行账号
       */
      accountNumber?: string
      /**
       * 账户性质
       */
      accountType?: string
      /**
       * 结余-预估
       */
      estimateBalanceAmount?: number
      /**
       * 结余受限-预估
       */
      estimateBalanceLimitAmount?: number
      /**
       * 结余-实际
       */
      actualBalanceAmount?: number
      /**
       * 待转余额
       */
      pendingBalanceAmount?: number
    }
  }[]
}

/**
 * 接口 [监管户待转资金列表每日情况展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26899) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/account/daily`
 * @更新时间 `2025-01-06 09:20:26`
 */
export interface FundTransferaccountDailyRequest {
  /**
   * accountId
   */
  accountId?: number
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 当日情况
   */
  currentDate?: string
  /**
   * 起止日期-开始
   */
  queryDateStart?: string
  /**
   * 起止日期-结束
   */
  queryDateEnd?: string
}

/**
 * 接口 [监管户待转资金列表每日情况展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26899) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/account/daily`
 * @更新时间 `2025-01-06 09:20:26`
 */
export interface FundTransferaccountDailyResponse {
  /**
   * 列表-以账户和日期为维度
   */
  list?: {
    /**
     * 沉淀时间
     */
    settingTime?: string
    /**
     * 占比
     */
    payAmount?: {
      /**
       * 数值
       */
      value?: string
      /**
       * 单位
       */
      unit?: string
    }
    /**
     * 沉淀金额
     */
    depositedAmount?: number
  }[]
  /**
   * 总额
   */
  sum?: number
}

/**
 * 接口 [监管户待转资金图表展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26887) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/graph/list`
 * @更新时间 `2024-12-30 09:10:16`
 */
export interface FundTransfergraphListRequest {
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 起止日期-开始
   */
  queryDateStart: string
  /**
   * 起止日期-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [监管户待转资金图表展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26887) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/graph/list`
 * @更新时间 `2024-12-30 09:10:16`
 */
export interface FundTransfergraphListResponse {
  /**
   * 合计-以日期为维度
   */
  sum?: {
    /**
     * 数据时点
     */
    date?: string
    /**
     * 总合计
     */
    allSum?: {
      /**
       * id
       */
      id?: number
      /**
       * 数据时点
       */
      date?: string
      /**
       * 账户基本表id
       */
      accountId?: number
      /**
       * 开户银行
       */
      accountBank?: string
      /**
       * 银行账号
       */
      accountNumber?: string
      /**
       * 账户性质
       */
      accountType?: string
      /**
       * 结余-预估
       */
      estimateBalanceAmount?: number
      /**
       * 结余受限-预估
       */
      estimateBalanceLimitAmount?: number
      /**
       * 结余-实际
       */
      actualBalanceAmount?: number
      /**
       * 差额
       */
      diffAmount?: number
      /**
       * 合计值-仅合计列表回返回
       */
      sum?: number
      /**
       * 待转余额
       */
      pendingBalanceAmount?: number
    }
  }[]
}

/**
 * 接口 [监管户待转资金图表当日情况展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26917) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/current/daily`
 * @更新时间 `2024-12-30 09:10:16`
 */
export interface FundTransfercurrentDailyRequest {
  /**
   * 当日情况
   */
  currentDate?: string
}

/**
 * 接口 [监管户待转资金图表当日情况展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26917) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/current/daily`
 * @更新时间 `2024-12-30 09:10:16`
 */
export interface FundTransfercurrentDailyResponse {
  /**
   * 列表-以账户和日期为维度
   */
  list?: {
    /**
     * 沉淀时间
     */
    settingTime?: string
    /**
     * 占比
     */
    payAmount?: {
      /**
       * 数值
       */
      value?: string
      /**
       * 单位
       */
      unit?: string
    }
    /**
     * 沉淀金额
     */
    depositedAmount?: number
  }[]
  /**
   * 总额
   */
  sum?: number
}

/**
 * 接口 [监管户待转资金图表每日情况展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26911) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/graph/daily`
 * @更新时间 `2024-12-30 09:10:16`
 */
export interface FundTransfergraphDailyRequest {
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 当日情况
   */
  currentDate?: string
  /**
   * 起止日期-开始
   */
  queryDateStart: string
  /**
   * 起止日期-结束
   */
  queryDateEnd: string
}

/**
 * 接口 [监管户待转资金图表每日情况展示↗](http://yapi.zswltec.com:3000/project/11/interface/api/26911) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/graph/daily`
 * @更新时间 `2024-12-30 09:10:16`
 */
export interface FundTransfergraphDailyResponse {
  /**
   * 列表-以账户和日期为维度
   */
  list?: {
    /**
     * 沉淀时间
     */
    settingTime?: string
    /**
     * 占比
     */
    payAmount?: {
      /**
       * 数值
       */
      value?: string
      /**
       * 单位
       */
      unit?: string
    }
    /**
     * 沉淀金额
     */
    depositedAmount?: number
  }[]
  /**
   * 总额
   */
  sum?: number
}

/**
 * 接口 [监管户待转资金账户详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26893) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/detail/list`
 * @更新时间 `2025-01-03 15:17:44`
 */
export interface FundTransferdetailListRequest {
  /**
   * accountId
   */
  accountId?: number
  /**
   * 开户银行
   */
  accountBank?: string
  /**
   * 起止日期-开始
   */
  queryDateStart?: string
  /**
   * 起止日期-结束
   */
  queryDateEnd?: string
}

/**
 * 接口 [监管户待转资金账户详情↗](http://yapi.zswltec.com:3000/project/11/interface/api/26893) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/detail/list`
 * @更新时间 `2025-01-03 15:17:44`
 */
export interface FundTransferdetailListResponse {
  /**
   * 列表-以账户和日期为维度
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 数据时点
     */
    date?: string
    /**
     * 账户基本表id
     */
    accountId?: number
    /**
     * 开户银行
     */
    accountBank?: string
    /**
     * 银行账号
     */
    accountNumber?: string
    /**
     * 账户性质
     */
    accountType?: string
    /**
     * 结余-预估
     */
    estimateBalanceAmount?: number
    /**
     * 结余受限-预估
     */
    estimateBalanceLimitAmount?: number
    /**
     * 结余-实际
     */
    actualBalanceAmount?: number
    /**
     * 差额
     */
    diffAmount?: number
    /**
     * 合计值-仅合计列表回返回
     */
    sum?: number
    /**
     * 待转余额
     */
    pendingBalanceAmount?: number
  }[]
}

/**
 * 接口 [账户列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26947) 的 **请求类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/bankAccount/list`
 * @更新时间 `2025-01-06 09:20:26`
 */
export interface FundTransferbankAccountListRequest {
  /**
   * 银行名称
   */
  bankName?: string
}

/**
 * 接口 [账户列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/26947) 的 **返回类型**
 *
 * @分类 [监管户待转资金Api↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_3734)
 * @请求头 `POST /fundTransfer/bankAccount/list`
 * @更新时间 `2025-01-06 09:20:26`
 */
export type FundTransferbankAccountListResponse = {
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
}[]

/* prettier-ignore-end */
