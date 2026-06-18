/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [所属企业↗](http://yapi.zswltec.com:3000/project/10/interface/api/16843) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/query/affiliated/enterprise`
 * @更新时间 `2023-12-07 09:14:08`
 */
export interface AffiliatedEnterpriseRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
}

/**
 * 接口 [所属企业↗](http://yapi.zswltec.com:3000/project/10/interface/api/16843) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/query/affiliated/enterprise`
 * @更新时间 `2023-12-07 09:14:08`
 */
export interface AffiliatedEnterpriseResponse {
  /**
   * 是否主企业，1 是，0不是
   */
  isAffiliated?: number
  /**
   * 所属企业名称
   */
  groupEnterpriseName?: string
}

/**
 * 接口 [批量填充所属企业及下属企业↗](http://yapi.zswltec.com:3000/project/10/interface/api/91) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/batch/query/associated/enterprise`
 * @更新时间 `2024-01-20 11:11:11`
 */
export type AssociatedEnterpriseRequest = {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
}[]

/**
 * 接口 [批量填充所属企业及下属企业↗](http://yapi.zswltec.com:3000/project/10/interface/api/91) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/batch/query/associated/enterprise`
 * @更新时间 `2024-01-20 11:11:11`
 */
export type AssociatedEnterpriseResponse = {
  /**
   * 是否主企业，1 是，0不是
   */
  isAffiliated?: number
  /**
   * 所属企业名称
   */
  groupEnterpriseName?: string
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  children?: {
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 统一社会信用代码
     */
    unifiedSocialCreditCode?: string
    children?: {}[]
  }[]
}[]

/**
 * 接口 [查询下属企业↗](http://yapi.zswltec.com:3000/project/10/interface/api/16849) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/query/associated/enterprise`
 * @更新时间 `2023-12-07 09:14:08`
 */
export interface AssociatedEnterpriseRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
}

/**
 * 接口 [查询下属企业↗](http://yapi.zswltec.com:3000/project/10/interface/api/16849) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/query/associated/enterprise`
 * @更新时间 `2023-12-07 09:14:08`
 */
export interface AssociatedEnterpriseResponse {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
  children?: {}[]
}

/**
 * 接口 [黑灰名单模糊查询企业信息↗](http://yapi.zswltec.com:3000/project/10/interface/api/16837) 的 **请求类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/query/vague/enterprise`
 * @更新时间 `2023-12-06 14:25:16`
 */
export interface VagueEnterpriseRequest {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
}

/**
 * 接口 [黑灰名单模糊查询企业信息↗](http://yapi.zswltec.com:3000/project/10/interface/api/16837) 的 **返回类型**
 *
 * @分类 [黑灰名单库-查询外部数据接口↗](http://yapi.zswltec.com:3000/project/10/interface/api/cat_3795)
 * @请求头 `POST /public/black/query/vague/enterprise`
 * @更新时间 `2023-12-06 14:25:16`
 */
export type VagueEnterpriseResponse = {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 统一社会信用代码
   */
  unifiedSocialCreditCode?: string
}[]

/* prettier-ignore-end */
