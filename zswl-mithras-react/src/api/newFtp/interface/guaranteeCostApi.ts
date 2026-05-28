/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改担保成本刷新↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12849) 的 **请求类型**
 *
 * @分类 [担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2473)
 * @请求头 `POST /new/ftp/guarantee/cost/pricing/flash`
 * @更新时间 `2023-05-24 19:49:50`
 */
export interface PricingFlashRequest {
  /**
   * id
   */
  id?: number
  /**
   * month
   */
  month?: string
  /**
   * 当期均值
   */
  currentAverage?: number
}

/**
 * 接口 [修改担保成本刷新↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12849) 的 **返回类型**
 *
 * @分类 [担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2473)
 * @请求头 `POST /new/ftp/guarantee/cost/pricing/flash`
 * @更新时间 `2023-05-24 19:49:50`
 */
export type PricingFlashResponse = null

/**
 * 接口 [修改担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12839) 的 **请求类型**
 *
 * @分类 [担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2473)
 * @请求头 `POST /new/ftp/guarantee/cost/pricing/modify`
 * @更新时间 `2023-05-24 19:49:49`
 */
export interface PricingModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * month
   */
  month?: string
  /**
   * 当期均值
   */
  currentAverage?: number
}

/**
 * 接口 [修改担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12839) 的 **返回类型**
 *
 * @分类 [担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2473)
 * @请求头 `POST /new/ftp/guarantee/cost/pricing/modify`
 * @更新时间 `2023-05-24 19:49:49`
 */
export type PricingModifyResponse = null

/**
 * 接口 [担保成本定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12844) 的 **请求类型**
 *
 * @分类 [担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2473)
 * @请求头 `POST /new/ftp/guarantee/cost/pricing/list`
 * @更新时间 `2023-05-24 19:49:49`
 */
export interface PricingListRequest {
  page?: number
  pageSize?: number
  version?: string
}

/**
 * 接口 [担保成本定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12844) 的 **返回类型**
 *
 * @分类 [担保成本定价↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2473)
 * @请求头 `POST /new/ftp/guarantee/cost/pricing/list`
 * @更新时间 `2023-05-24 19:49:49`
 */
export interface PricingListResponse {
  /**
   * 查询集合
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * month
     */
    month?: string
    /**
     * 当期均值
     */
    currentAverage?: number
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
