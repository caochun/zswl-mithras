/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [1年期SHIBOR利率列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12684) 的 **请求类型**
 *
 * @分类 [new-ftp-shibor-interest-rate-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2403)
 * @请求头 `POST /new/ftp/shibor/interest/rate/list`
 * @更新时间 `2023-05-23 09:47:10`
 */
export interface RateListRequest {
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
 * 接口 [1年期SHIBOR利率列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12684) 的 **返回类型**
 *
 * @分类 [new-ftp-shibor-interest-rate-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2403)
 * @请求头 `POST /new/ftp/shibor/interest/rate/list`
 * @更新时间 `2023-05-23 09:47:10`
 */
export interface RateListResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 日期
     */
    date?: string
    /**
     * SHIBOR利率值
     */
    value?: number
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
 * 接口 [一年期shibor利率定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12689) 的 **请求类型**
 *
 * @分类 [new-ftp-shibor-interest-rate-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2403)
 * @请求头 `POST /new/ftp/shibor/interest/rate/listprincing`
 * @更新时间 `2023-05-23 09:47:10`
 */
export interface RateListprincingRequest {
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
 * 接口 [一年期shibor利率定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12689) 的 **返回类型**
 *
 * @分类 [new-ftp-shibor-interest-rate-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2403)
 * @请求头 `POST /new/ftp/shibor/interest/rate/listprincing`
 * @更新时间 `2023-05-23 09:47:10`
 */
export interface RateListprincingResponse {
  /**
   * 查询集合 ,T
   */
  list?: {
    /**
     * id
     */
    id?: number
    /**
     * 月份
     */
    month?: string
    /**
     * ftp定价
     */
    ftpPricing?: number
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
 * 接口 [新增1年期SHIBOR利率↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12679) 的 **请求类型**
 *
 * @分类 [new-ftp-shibor-interest-rate-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2403)
 * @请求头 `POST /new/ftp/shibor/interest/rate/import`
 * @更新时间 `2023-05-23 09:47:10`
 */
export interface RateImportRequest {
  /**
   * (MultipartFile)
   */
  file: string
}

/**
 * 接口 [新增1年期SHIBOR利率↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12679) 的 **返回类型**
 *
 * @分类 [new-ftp-shibor-interest-rate-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2403)
 * @请求头 `POST /new/ftp/shibor/interest/rate/import`
 * @更新时间 `2023-05-23 09:47:10`
 */
export interface RateImportResponse {}

/* prettier-ignore-end */
