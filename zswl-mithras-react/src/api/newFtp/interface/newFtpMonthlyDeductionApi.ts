/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [修改月度计价指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12739) 的 **请求类型**
 *
 * @分类 [new-ftp-monthly-deduction-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2428)
 * @请求头 `POST /new/ftp/monthly/deduction/modify`
 * @更新时间 `2023-05-23 09:47:53`
 */
export interface DeductionModifyRequest {
  /**
   * id
   */
  id?: number
  /**
   * 融资成本
   */
  financingCost?: number
  /**
   * 担保成本
   */
  guaranteeCost?: number
  /**
   * 成本费用计价-小计
   */
  subtotalCost?: number
  /**
   * 10年期国债收益率
   */
  treasuryBondYield?: number
  /**
   * 10年期国债收益率-权重
   */
  treasuryBondYieldWeight?: number
  /**
   * 1年期shibor利率
   */
  shiborRate?: number
  /**
   * 1年期shibor利率-权重
   */
  shiborRateWeight?: number
  /**
   * 同期lpr利率
   */
  lprRate?: number
  /**
   * 同期lpr利率-权重
   */
  lprRateWeight?: number
  /**
   * 融资成本趋势
   */
  financingCostTrends?: number
  /**
   * 融资成本趋势-权重
   */
  financingCostTrendsWeight?: number
  /**
   * 金融市场波动计价-小计
   */
  subtotalRate?: number
  /**
   * 产业类-资产行业计价-鼓励介入类
   */
  assetEncourage?: number
  /**
   * 产业类-资产行业计价-适度支持类
   */
  assetModerate?: number
  /**
   * 产业类-资产行业计价-谨慎支持类
   */
  assetCautious?: number
  /**
   * 产业类-地区分类计价-浙江地区
   */
  industryRegionZhejiang?: number
  /**
   * 产业类-地区分类计价-鼓励支持类地区（除浙江）
   */
  industryRegionEncourage?: number
  /**
   * 产业类-地区分类计价-其他地区
   */
  industryRegionOther?: number
  /**
   * 产业类-客户主体计价-上市公司
   */
  customerListed?: number
  /**
   * 产业类-客户主体计价-国有企业
   */
  customerStateOwned?: number
  /**
   * 产业类-客户主体计价-其他类
   */
  customerStateOther?: number
  /**
   * 公共事业类（含民生消费类）-地区分类计价-浙江地区
   */
  publicUtilitiesRegionZhejiang?: number
  /**
   * 公共事业类（含民生消费类）-地区分类计价-鼓励支持类地区（除浙江）
   */
  publicUtilitiesRegionEncourage?: number
  /**
   * 公共事业类（含民生消费类）-地区分类计价-其他地区
   */
  publicUtilitiesRegionOther?: number
}

/**
 * 接口 [修改月度计价指导↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12739) 的 **返回类型**
 *
 * @分类 [new-ftp-monthly-deduction-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2428)
 * @请求头 `POST /new/ftp/monthly/deduction/modify`
 * @更新时间 `2023-05-23 09:47:53`
 */
export interface DeductionModifyResponse {}

/**
 * 接口 [月度计价指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12744) 的 **请求类型**
 *
 * @分类 [new-ftp-monthly-deduction-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2428)
 * @请求头 `POST /new/ftp/monthly/deduction/list`
 * @更新时间 `2023-05-23 09:47:53`
 */
export interface DeductionListRequest {
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
 * 接口 [月度计价指导列表↗](http://api-dev.zswl.cn:3011/project/11/interface/api/12744) 的 **返回类型**
 *
 * @分类 [new-ftp-monthly-deduction-api↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_2428)
 * @请求头 `POST /new/ftp/monthly/deduction/list`
 * @更新时间 `2023-05-23 09:47:53`
 */
export type DeductionListResponse = {
  /**
   * id
   */
  id?: number
  /**
   * 所属指引id
   */
  ftpId?: number
  /**
   * 期限
   */
  termRange?: string
  /**
   * 融资成本
   */
  financingCost?: number
  /**
   * 担保成本
   */
  guaranteeCost?: number
  /**
   * 成本费用计价-小计
   */
  subtotalCost?: number
  /**
   * 10年期国债收益率
   */
  treasuryBondYield?: number
  /**
   * 10年期国债收益率-权重
   */
  treasuryBondYieldWeight?: number
  /**
   * 1年期shibor利率
   */
  shiborRate?: number
  /**
   * 1年期shibor利率-权重
   */
  shiborRateWeight?: number
  /**
   * 同期lpr利率
   */
  lprRate?: number
  /**
   * 同期lpr利率-权重
   */
  lprRateWeight?: number
  /**
   * 融资成本趋势
   */
  financingCostTrends?: number
  /**
   * 融资成本趋势-权重
   */
  financingCostTrendsWeight?: number
  /**
   * 金融市场波动计价-小计
   */
  subtotalRate?: number
  /**
   * 产业类-资产行业计价-鼓励介入类
   */
  assetEncourage?: number
  /**
   * 产业类-资产行业计价-适度支持类
   */
  assetModerate?: number
  /**
   * 产业类-资产行业计价-谨慎支持类
   */
  assetCautious?: number
  /**
   * 产业类-地区分类计价-浙江地区
   */
  industryRegionZhejiang?: number
  /**
   * 产业类-地区分类计价-鼓励支持类地区（除浙江）
   */
  industryRegionEncourage?: number
  /**
   * 产业类-地区分类计价-其他地区
   */
  industryRegionOther?: number
  /**
   * 产业类-客户主体计价-上市公司
   */
  customerListed?: number
  /**
   * 产业类-客户主体计价-国有企业
   */
  customerStateOwned?: number
  /**
   * 产业类-客户主体计价-其他类
   */
  customerStateOther?: number
  /**
   * 公共事业类（含民生消费类）-地区分类计价-浙江地区
   */
  publicUtilitiesRegionZhejiang?: number
  /**
   * 公共事业类（含民生消费类）-地区分类计价-鼓励支持类地区（除浙江）
   */
  publicUtilitiesRegionEncourage?: number
  /**
   * 公共事业类（含民生消费类）-地区分类计价-其他地区
   */
  publicUtilitiesRegionOther?: number
}[]

/* prettier-ignore-end */
