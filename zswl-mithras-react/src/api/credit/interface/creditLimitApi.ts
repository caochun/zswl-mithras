/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [征信报告-信用额度表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38329) 的 **请求类型**
 *
 * @分类 [征信报告-信用额度表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5474)
 * @请求头 `POST /credit/report/limit/list`
 * @更新时间 `2025-11-28 16:59:33`
 */
export interface LimitListRequest {
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
 * 接口 [征信报告-信用额度表列表↗](http://yapi.zswltec.com:3000/project/11/interface/api/38329) 的 **返回类型**
 *
 * @分类 [征信报告-信用额度表-接口↗](http://yapi.zswltec.com:3000/project/11/interface/api/cat_5474)
 * @请求头 `POST /credit/report/limit/list`
 * @更新时间 `2025-11-28 16:59:33`
 */
export interface LimitListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 查询编号
     */
    creditCode?: number
    /**
     * 征信报告基本表id
     */
    creditReportId?: number
    /**
     * 非循环-总额
     */
    totalAmount?: number
    /**
     * 非循环-已用额度
     */
    usedAmount?: number
    /**
     * 非循环-剩余可用额度
     */
    remainingAvailableAmount?: number
    /**
     * 循环-已用额度
     */
    cycleTotalAmount?: number
    /**
     * 循环-已用额度
     */
    cycleUsedAmount?: number
    /**
     * 循环-已用额度
     */
    cycleRemainingAvailableAmount?: number
    /**
     * 逻辑删除，0-未删除，1-已删除
     */
    deleted?: number
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
  others?: {
    KEY?: {}
  }
}

/* prettier-ignore-end */
