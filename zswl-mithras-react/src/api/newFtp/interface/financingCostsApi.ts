/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12824) 的 **请求类型**
 *
 * @分类 [融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2468)
 * @请求头 `POST /new/ftp/financing/cost/pricing/modify`
 * @更新时间 `2023-05-25 13:58:21`
 */
export interface PricingModifyRequest {
  /**
   * month
   */
  month?: string
  /**
   * 一年当期均值
   */
  oneCurrentAverage?: number
  /**
   * 一年当年均值
   */
  oneAnnualAverage?: number
  /**
   * 三年当期均值
   */
  threeCurrentAverage?: number
  /**
   * 三年当年均值
   */
  threeAnnualAverage?: number
  /**
   * 五年当期均值
   */
  fiveCurrentAverage?: number
  /**
   * 五年当年均值
   */
  fiveAnnualAverage?: number
}

/**
 * 接口 [修改融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12824) 的 **返回类型**
 *
 * @分类 [融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2468)
 * @请求头 `POST /new/ftp/financing/cost/pricing/modify`
 * @更新时间 `2023-05-25 13:58:21`
 */
export type PricingModifyResponse = null

/**
 * 接口 [融资成本定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12829) 的 **请求类型**
 *
 * @分类 [融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2468)
 * @请求头 `POST /new/ftp/financing/cost/pricing/list`
 * @更新时间 `2023-05-25 13:58:21`
 */
export interface PricingListRequest {
  page?: number
  pageSize?: number
  version?: string
}

/**
 * 接口 [融资成本定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12829) 的 **返回类型**
 *
 * @分类 [融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2468)
 * @请求头 `POST /new/ftp/financing/cost/pricing/list`
 * @更新时间 `2023-05-25 13:58:21`
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
        /**
         * 类型
         * {@link TermRange#name()}
         */
        termRange?: string
        /**
         * 当期均值
         */
        currentAverage?: number
        /**
         * 当年均值
         */
        annualAverage?: number
        /**
         * ftp_pricing
         */
        ftpPricing?: number
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
 * 接口 [融资成本定价刷新↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12834) 的 **请求类型**
 *
 * @分类 [融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2468)
 * @请求头 `POST /new/ftp/financing/cost/pricing/flash`
 * @更新时间 `2023-05-25 13:58:21`
 */
export interface PricingFlashRequest {
  /**
   * 月份
   */
  month?: string
}

/**
 * 接口 [融资成本定价刷新↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12834) 的 **返回类型**
 *
 * @分类 [融资成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2468)
 * @请求头 `POST /new/ftp/financing/cost/pricing/flash`
 * @更新时间 `2023-05-25 13:58:21`
 */
export type PricingFlashResponse = null

/* prettier-ignore-end */
