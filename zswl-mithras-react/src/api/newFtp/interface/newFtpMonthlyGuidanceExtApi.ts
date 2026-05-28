/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [ftp指导报价扩展表（下半部分）列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12724) 的 **请求类型**
 *
 * @分类 [new-ftp-monthly-guidance-ext-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2418)
 * @请求头 `POST /new/ftp/monthly/guidance/ext/detail`
 * @更新时间 `2023-05-23 09:47:45`
 */
export interface ExtDetailRequest {
  /**
   * 主表id
   */
  mainId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp指导报价扩展表（下半部分）列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12724) 的 **返回类型**
 *
 * @分类 [new-ftp-monthly-guidance-ext-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2418)
 * @请求头 `POST /new/ftp/monthly/guidance/ext/detail`
 * @更新时间 `2023-05-23 09:47:45`
 */
export interface ExtDetailResponse {
  /**
   * id
   */
  id?: number
  /**
   * 所属的主数据id
   */
  ftpId?: number
  /**
   * 一年期（含）以内
   */
  oneYear?: number
  /**
   * 一至三年期（含）
   */
  oneToThreeYear?: number
  /**
   * 三年以上
   */
  moreThanThreeYear?: number
  /**
   * 卖出价
   */
  sellingPrice?: number
  /**
   * 买入价
   */
  buyingPrice?: number
  /**
   * 买入价描述, 若buyingPrice为空，则在页面展示buyingPriceDisplay
   */
  buyingPriceDisplay?: string
}

/**
 * 接口 [修改ftp指导报价扩展表（下半部分）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12719) 的 **请求类型**
 *
 * @分类 [new-ftp-monthly-guidance-ext-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2418)
 * @请求头 `POST /new/ftp/monthly/guidance/ext/modify`
 * @更新时间 `2023-05-23 09:47:45`
 */
export interface ExtModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 一年期（含）以内
   */
  oneYear?: number
  /**
   * 一至三年期（含）
   */
  oneToThreeYear?: number
  /**
   * 三年以上
   */
  moreThanThreeYear?: number
  /**
   * 卖出价
   */
  sellingPrice?: number
  /**
   * 买入价
   */
  buyingPrice?: number
}

/**
 * 接口 [修改ftp指导报价扩展表（下半部分）↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12719) 的 **返回类型**
 *
 * @分类 [new-ftp-monthly-guidance-ext-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2418)
 * @请求头 `POST /new/ftp/monthly/guidance/ext/modify`
 * @更新时间 `2023-05-23 09:47:45`
 */
export interface ExtModifyResponse {}

/* prettier-ignore-end */
