/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [财务管理-汇率设置-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37471) 的 **请求类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/pageList`
 * @更新时间 `2025-09-25 10:24:17`
 */
export interface ExchangeRatePageListRequest {
  /**
   * 年份
   */
  year?: number
  /**
   * 月份
   */
  month?: number
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
 * 接口 [财务管理-汇率设置-分页列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/37471) 的 **返回类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/pageList`
 * @更新时间 `2025-09-25 10:24:17`
 */
export interface ExchangeRatePageListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * 主键id
     */
    id?: number
    /**
     * 年份
     */
    targetYear?: number
    /**
     * 月份
     */
    targetMonth?: number
    /**
     * 汇率日期
     */
    targetDate?: string
    /**
     * 币种
     */
    currency?: string
    /**
     * 汇率
     */
    exchangeRate?: number
    /**
     * 更新时间
     */
    updateTime?: string
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
  others?: {}
}

/**
 * 接口 [财务管理-汇率设置-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37465) 的 **请求类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/delete`
 * @更新时间 `2025-09-24 15:16:03`
 */
export interface ExchangeRateDeleteRequest {
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
 * 接口 [财务管理-汇率设置-删除↗](http://yapi.zswltec.com:3000/project/11/interface/api/37465) 的 **返回类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/delete`
 * @更新时间 `2025-09-24 15:16:03`
 */
export interface ExchangeRateDeleteResponse {
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
 * 接口 [财务管理-汇率设置-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/37459) 的 **请求类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/add`
 * @更新时间 `2025-09-25 10:24:17`
 */
export interface ExchangeRateAddRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 年份
   */
  targetYear: number
  /**
   * 月份
   */
  targetMonth: number
  /**
   * 汇率日期
   */
  targetDate: string
  /**
   * 币种
   */
  currency: string
  /**
   * 汇率
   */
  exchangeRate: number
  /**
   * 是否草稿数据（待办中新增的先保存为草稿数据，需要传1）
   */
  isDraft: number
}

/**
 * 接口 [财务管理-汇率设置-新增↗](http://yapi.zswltec.com:3000/project/11/interface/api/37459) 的 **返回类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/add`
 * @更新时间 `2025-09-25 10:24:17`
 */
export type ExchangeRateAddResponse = number

/**
 * 接口 [财务管理-汇率设置-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/37477) 的 **请求类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/modify`
 * @更新时间 `2025-09-25 10:24:17`
 */
export interface ExchangeRateModifyRequest {
  /**
   * 主键id
   */
  id?: number
  /**
   * 年份
   */
  targetYear: number
  /**
   * 月份
   */
  targetMonth: number
  /**
   * 汇率日期
   */
  targetDate: string
  /**
   * 币种
   */
  currency: string
  /**
   * 汇率
   */
  exchangeRate: number
  /**
   * 是否草稿数据（待办中新增的先保存为草稿数据，需要传1）
   */
  isDraft: number
}

/**
 * 接口 [财务管理-汇率设置-编辑↗](http://yapi.zswltec.com:3000/project/11/interface/api/37477) 的 **返回类型**
 *
 * @分类 [财务管理-汇率设置↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5330)
 * @请求头 `POST /baseData/exchangeRate/modify`
 * @更新时间 `2025-09-25 10:24:17`
 */
export type ExchangeRateModifyResponse = number

/* prettier-ignore-end */
