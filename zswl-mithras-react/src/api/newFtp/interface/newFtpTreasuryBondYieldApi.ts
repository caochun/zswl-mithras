/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [10年期国债收益率列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12699) 的 **请求类型**
 *
 * @分类 [new-ftp-treasury-bond-yield-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2408)
 * @请求头 `POST /new/ftp/treasury/bond/yield/list`
 * @更新时间 `2023-05-23 09:47:16`
 */
export interface YieldListRequest {
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
 * 接口 [10年期国债收益率列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12699) 的 **返回类型**
 *
 * @分类 [new-ftp-treasury-bond-yield-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2408)
 * @请求头 `POST /new/ftp/treasury/bond/yield/list`
 * @更新时间 `2023-05-23 09:47:16`
 */
export interface YieldListResponse {
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
     * 收益率值
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
 * 接口 [10年期国债收益率定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12704) 的 **请求类型**
 *
 * @分类 [new-ftp-treasury-bond-yield-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2408)
 * @请求头 `POST /new/ftp/treasury/bond/yield/listpricing`
 * @更新时间 `2023-05-23 09:47:16`
 */
export interface YieldListpricingRequest {
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
 * 接口 [10年期国债收益率定价列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12704) 的 **返回类型**
 *
 * @分类 [new-ftp-treasury-bond-yield-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2408)
 * @请求头 `POST /new/ftp/treasury/bond/yield/listpricing`
 * @更新时间 `2023-05-23 09:47:16`
 */
export interface YieldListpricingResponse {
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
     * 波动幅度
     */
    fluctuationRange?: number
    /**
     * ftp计价
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
 * 接口 [导入10年期国债收益率↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12694) 的 **请求类型**
 *
 * @分类 [new-ftp-treasury-bond-yield-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2408)
 * @请求头 `POST /new/ftp/treasury/bond/yield/import`
 * @更新时间 `2023-05-23 09:47:16`
 */
export interface YieldImportRequest {
  /**
   * (MultipartFile)
   */
  file: string
}

/**
 * 接口 [导入10年期国债收益率↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12694) 的 **返回类型**
 *
 * @分类 [new-ftp-treasury-bond-yield-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2408)
 * @请求头 `POST /new/ftp/treasury/bond/yield/import`
 * @更新时间 `2023-05-23 09:47:16`
 */
export interface YieldImportResponse {}

/* prettier-ignore-end */
