/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [获取最新财年↗](http://yapi.zswltec.com:3000/project/69/interface/api/27097) 的 **请求类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/latestYear`
 * @更新时间 `2025-01-14 16:43:35`
 */
export interface ComparisonLatestYearRequest {}

/**
 * 接口 [获取最新财年↗](http://yapi.zswltec.com:3000/project/69/interface/api/27097) 的 **返回类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/latestYear`
 * @更新时间 `2025-01-14 16:43:35`
 */
export type ComparisonLatestYearResponse = string[]

/**
 * 接口 [企业状态↗](http://yapi.zswltec.com:3000/project/69/interface/api/27103) 的 **请求类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/enterpriseState`
 * @更新时间 `2025-01-14 16:43:36`
 */
export interface ComparisonEnterpriseStateRequest {
  /**
   * 财年
   */
  busiDate: string
  /**
   * 用户账号
   */
  account?: string
}

/**
 * 接口 [企业状态↗](http://yapi.zswltec.com:3000/project/69/interface/api/27103) 的 **返回类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/enterpriseState`
 * @更新时间 `2025-01-14 16:43:36`
 */
export type ComparisonEnterpriseStateResponse = {
  /**
   * 企业编号
   */
  enterpriseCode?: string
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 企业状态  ready(财报已上报)  wait（等待财报上报）
   */
  status?: string
  /**
   * 勾选状态
   */
  choice?: boolean
}[]

/**
 * 接口 [同业比较分析↗](http://yapi.zswltec.com:3000/project/69/interface/api/27091) 的 **请求类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison`
 * @更新时间 `2025-01-14 16:43:35`
 */
export interface PeerComparisonRequest {
  busiDate: string
  /**
   * 企业名单
   */
  enterpriseCodes: string[]
  /**
   * 用户账号
   */
  account?: string
}

/**
 * 接口 [同业比较分析↗](http://yapi.zswltec.com:3000/project/69/interface/api/27091) 的 **返回类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison`
 * @更新时间 `2025-01-14 16:43:35`
 */
export interface PeerComparisonResponse {
  /**
   * 同业指标列表
   */
  peers?: {
    /**
     * 企业名称
     */
    enterpriseName?: string
    /**
     * 资产收益率  净利润/总资产
     */
    roa?: string
    /**
     * 净资产收益率  净利润/净资产
     */
    roe?: string
    /**
     * 总资产(亿元)
     */
    totalAssets?: string
    /**
     * 净资产（亿元）
     */
    netAssets?: string
    /**
     * 净利润（亿元）
     */
    netProfit?: string
    /**
     * 杠杆率  总资产/净资产
     */
    leverageRatio?: string
  }[]
  /**
   * 数据范围
   */
  range?: {
    minRoa?: string
    maxRoa?: string
    minRoe?: string
    maxRoe?: string
    minTotalAssets?: string
    maxTotalAssets?: string
    minNetAssets?: string
    maxNetAssets?: string
    minNetProfit?: string
    maxNetProfit?: string
  }
}

/**
 * 接口 [明细数据集合↗](http://yapi.zswltec.com:3000/project/69/interface/api/27127) 的 **请求类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/list`
 * @更新时间 `2025-01-14 16:43:36`
 */
export interface ComparisonListRequest {
  /**
   * 财年
   */
  busiDate: string
  /**
   * 企业名称
   */
  enterpriseName?: string
}

/**
 * 接口 [明细数据集合↗](http://yapi.zswltec.com:3000/project/69/interface/api/27127) 的 **返回类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/list`
 * @更新时间 `2025-01-14 16:43:36`
 */
export type ComparisonListResponse = {
  /**
   * 企业名称
   */
  enterpriseName?: string
  /**
   * 资产收益率  净利润/总资产
   */
  roa?: string
  /**
   * 净资产收益率  净利润/净资产
   */
  roe?: string
  /**
   * 总资产(亿元)
   */
  totalAssets?: string
  /**
   * 净资产（亿元）
   */
  netAssets?: string
  /**
   * 净利润（亿元）
   */
  netProfit?: string
  /**
   * 杠杆率  总资产/净资产
   */
  leverageRatio?: string
}[]

/**
 * 接口 [用户对标企业配置↗](http://yapi.zswltec.com:3000/project/69/interface/api/27133) 的 **请求类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/config`
 * @更新时间 `2025-01-14 16:43:36`
 */
export interface ComparisonConfigRequest {
  /**
   * 选中的企业
   */
  enterpriseCodes: string[]
  /**
   * 用户账号
   */
  account?: string
}

/**
 * 接口 [用户对标企业配置↗](http://yapi.zswltec.com:3000/project/69/interface/api/27133) 的 **返回类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/config`
 * @更新时间 `2025-01-14 16:43:36`
 */
export interface ComparisonConfigResponse {}

/**
 * 接口 [财年数据刷新↗](http://yapi.zswltec.com:3000/project/69/interface/api/27109) 的 **请求类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/refresh`
 * @更新时间 `2025-01-14 16:43:36`
 */
export interface ComparisonRefreshRequest {
  /**
   * 财年
   */
  busiDate?: string
}

/**
 * 接口 [财年数据刷新↗](http://yapi.zswltec.com:3000/project/69/interface/api/27109) 的 **返回类型**
 *
 * @分类 [PeerComparisonController↗](http://yapi.zswltec.com:3000/project/69/interface/api/cat_3794)
 * @请求头 `POST /peer/comparison/refresh`
 * @更新时间 `2025-01-14 16:43:36`
 */
export interface ComparisonRefreshResponse {}

/* prettier-ignore-end */
