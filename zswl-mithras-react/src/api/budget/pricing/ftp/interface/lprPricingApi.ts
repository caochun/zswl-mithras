/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [LPR定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12819) 的 **请求类型**
 *
 * @分类 [LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2458)
 * @请求头 `POST /new/ftp/lpr/pricing/list`
 * @更新时间 `2023-05-25 13:57:50`
 */
export interface PricingListRequest {
  page?: number
  pageSize?: number
  version?: string
}

/**
 * 接口 [LPR定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12819) 的 **返回类型**
 *
 * @分类 [LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2458)
 * @请求头 `POST /new/ftp/lpr/pricing/list`
 * @更新时间 `2023-05-25 13:57:50`
 */
export interface PricingListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * month
     */
    month?: string
    bodyMap?: {
      key?: {
        /**
         * id
         */
        id?: number
        termRange?: string
        /**
         * lpr
         */
        lpr?: number
        lprPricing?: number
      }
    }
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
 * 接口 [修改LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12814) 的 **请求类型**
 *
 * @分类 [LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2458)
 * @请求头 `POST /new/ftp/lpr/pricing/modify`
 * @更新时间 `2023-05-25 13:57:50`
 */
export interface PricingModifyRequest {
  /**
   * month
   */
  month?: string
  /**
   * 一年期lpr
   */
  lprOneYear?: number
  /**
   * 五年期lpr
   */
  lprFiveYear?: number
}

/**
 * 接口 [修改LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12814) 的 **返回类型**
 *
 * @分类 [LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2458)
 * @请求头 `POST /new/ftp/lpr/pricing/modify`
 * @更新时间 `2023-05-25 13:57:50`
 */
export type PricingModifyResponse = null

/**
 * 接口 [新增LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12809) 的 **请求类型**
 *
 * @分类 [LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2458)
 * @请求头 `POST /new/ftp/lpr/pricing/add`
 * @更新时间 `2023-05-25 13:57:49`
 */
export interface PricingAddRequest {
  /**
   * month
   */
  month?: string
  lprOneYear?: number
  lprFiveYear?: number
}

/**
 * 接口 [新增LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12809) 的 **返回类型**
 *
 * @分类 [LPR定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2458)
 * @请求头 `POST /new/ftp/lpr/pricing/add`
 * @更新时间 `2023-05-25 13:57:49`
 */
export type PricingAddResponse = null

/* prettier-ignore-end */
