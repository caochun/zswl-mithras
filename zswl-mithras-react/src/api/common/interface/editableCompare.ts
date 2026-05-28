/* prettier-ignore-start */
/* tslint:disable */
/* eslint-disable */

/* 该文件由 yapi-to-typescript 自动生成，请勿直接修改！！！ */

// @ts-ignore

/**
 * 接口 [ftp季度指导-基本报价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11630) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/base/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface PricingCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp季度指导-基本报价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11630) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/base/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export type PricingCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [ftp季度指导-客户主体计价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11631) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/customer/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface PricingCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp季度指导-客户主体计价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11631) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/customer/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export type PricingCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [ftp季度指导-按企业报价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11633) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/enterprise/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface PricingCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp季度指导-按企业报价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11633) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/enterprise/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export type PricingCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [ftp季度指导-按月报价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11632) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/month/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface PricingCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp季度指导-按月报价-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11632) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/quarterly/month/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export type PricingCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [ftp月度指导-pricing-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11635) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/monthly/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface PricingCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp月度指导-pricing-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11635) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/monthly/pricing/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export type PricingCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [ftp月度指导-valuation-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11636) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/monthly/valuation/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface ValuationCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp月度指导-valuation-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11636) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/monthly/valuation/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export type ValuationCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [ftp月度指导-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11634) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/monthly/guidance/detail/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface DetailCompareRequest {
  /**
   * 指导id
   */
  id: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [ftp月度指导-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11634) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /ftp/monthly/guidance/detail/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [付款申请-比对↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6224) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /payment/baseinfo/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface BaseinfoCompareRequest {
  /**
   * 付款申请ID
   */
  id: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [付款申请-比对↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6224) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /payment/baseinfo/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface BaseinfoCompareResponse {
  KEY?: {}
}

/**
 * 接口 [保理报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2036) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/factoring/price/detail/compare`
 * @更新时间 `2022-08-08 11:52:10`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [保理报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2036) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/factoring/price/detail/compare`
 * @更新时间 `2022-08-08 11:52:10`
 */
export type DetailCompareResponse = {
  diffData?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}[]

/**
 * 接口 [债权转让报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2032) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/aoc/price/detail/compare`
 * @更新时间 `2022-08-08 11:52:10`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [债权转让报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2032) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/aoc/price/detail/compare`
 * @更新时间 `2022-08-08 11:52:10`
 */
export type DetailCompareResponse = {
  diffData?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}[]

/**
 * 接口 [关联企业-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2060) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/related/enterprise/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareRequest {
  /**
   * clientId
   */
  clientId: number
  /**
   * 持股比例:shareholdingRatio,注册资本：registerCapital,投资金额：investAmount
   */
  orderField?: string
  /**
   * 倒排：desc，正排：asc
   */
  orderType?: string
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
 * 接口 [关联企业-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2060) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/related/enterprise/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [发债及评级信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2068) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/bond/info/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareRequest {
  /**
   * clientId
   */
  clientId: number
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
 * 接口 [发债及评级信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2068) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/bond/info/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [合同-基本信息详情-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6788) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-基本信息详情-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6788) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [合同-承租人表列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6444) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/tenantry/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-承租人表列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6444) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/tenantry/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [合同-报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6784) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/price/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareRequest {
  /**
   * 所属的contractId
   */
  contractId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6784) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/price/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareResponse {
  /**
   * 租赁报价方案返回体
   */
  leasePriceModifyRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
  /**
   * 债权转让请求体
   */
  aocPriceRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
  /**
   * 保理方案请求体
   */
  factoringPriceRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}

/**
 * 接口 [合同-抵押措施列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6452) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/mortgage/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-抵押措施列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6452) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/mortgage/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [合同-担保措施列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6704) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/guarantor/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-担保措施列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6704) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/guarantor/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [合同-收款账户表列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6440) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/account/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 合同id
   */
  contractId: number
  /**
   * 账户用途
   */
  accountUse: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-收款账户表列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6440) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/account/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [合同-最近一次结清方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11629) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/settle/plan/latest/get/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface GetCompareRequest {
  /**
   * 结清方案类型 SETTLE_NORMAL-正常结清 SETTLE_IN_ADVANCE-提前结清
   */
  planType: string
  /**
   * 主合同id
   */
  contractId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-最近一次结清方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/11629) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/settle/plan/latest/get/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface GetCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [合同-概算租金表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6780) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/rent/estimate/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 主合同id
   */
  contractId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-概算租金表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6780) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/rent/estimate/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareResponse {
  KEY?: {
    lsitMap?: {
      KEY?: {
        beforeValue?: {}
        value?: {}
        isChange?: boolean
      }
    }[]
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [合同-租赁物清单列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6708) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/leaseitem/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 主合同id
   */
  contractId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-租赁物清单列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6708) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/leaseitem/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [合同-质押措施列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6448) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/pledge/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 合同id
   */
  contractId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [合同-质押措施列表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6448) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/pledge/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [地址信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2076) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/address/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 客户id
   */
  clientId: number
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
 * 接口 [地址信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2076) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/address/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [实际租金表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6436) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/rent/actual/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 主合同id
   */
  contractId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [实际租金表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6436) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /contract/rent/actual/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    lsitMap?: {
      KEY?: {
        beforeValue?: {}
        value?: {}
        isChange?: boolean
      }
    }[]
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/3080) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/price/detail/compare`
 * @更新时间 `2023-01-13 15:17:56`
 */
export interface DetailCompareRequest {
  /**
   * 所属立项Id
   */
  projEstablishId?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/3080) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/price/detail/compare`
 * @更新时间 `2023-01-13 15:17:56`
 */
export interface DetailCompareResponse {
  /**
   * 租赁报价方案
   */
  leasePriceRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
  /**
   * 债权转让报价方案
   */
  aocPriceRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
  /**
   * 保理报价方案
   */
  factoringPriceRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}

/**
 * 接口 [法人工商信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2048) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/commerce/detail/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetailCompareRequest {
  /**
   * 客户id
   */
  clientId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [法人工商信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2048) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/commerce/detail/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [租赁报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2028) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/lease/price/detail/compare`
 * @更新时间 `2022-08-08 11:52:10`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [租赁报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2028) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/lease/price/detail/compare`
 * @更新时间 `2022-08-08 11:52:10`
 */
export type DetailCompareResponse = {
  diffData?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}[]

/**
 * 接口 [立项基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2024) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [立项基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2024) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/establish/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [联系人信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2064) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/contact/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareRequest {
  /**
   * 客户id
   */
  clientId: number
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
 * 接口 [联系人信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2064) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/contact/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [股东信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2056) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/shareholder/info/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareRequest {
  /**
   * clientId
   */
  clientId: number
  /**
   * 排序字段：认缴金额：paidTotal，出资占比：capitalPercent，实缴金额：actualPaidTotal
   */
  orderField?: string
  /**
   * 排序类型，倒排：desc，正排：asc
   */
  orderType?: string
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
 * 接口 [股东信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2056) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/shareholder/info/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [自然人基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2044) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /normal/base/info/detai/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetaiCompareRequest {
  /**
   * 客户id
   */
  clientId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [自然人基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2044) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /normal/base/info/detai/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetaiCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [自然人配偶信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2040) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /normal/spouse/detail/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetailCompareRequest {
  /**
   * 客户id
   */
  clientId: number
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
 * 接口 [自然人配偶信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2040) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /normal/spouse/detail/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface DetailCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [自然人银行卡信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2052) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /normal/bank/account/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareRequest {
  /**
   * 客户id
   */
  clientId: number
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
 * 接口 [自然人银行卡信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2052) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /normal/bank/account/list/compare`
 * @更新时间 `2023-01-13 15:17:57`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [获取现金流计划表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6432) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/cashflowplan/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 项目评审记录id
   */
  projReviewId: number
  /**
   * 流程id
   */
  processInstanceId?: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [获取现金流计划表-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/6432) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/cashflowplan/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export type ListCompareResponse = {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}[]

/**
 * 接口 [银行账号信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2072) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/bank/account/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareRequest {
  /**
   * 客户id
   */
  clientId: number
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
 * 接口 [银行账号信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2072) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /corp/bank/account/list/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface ListCompareResponse {
  /**
   * 查询集合
   */
  list?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
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
 * 接口 [集团授信立项-基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10944) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /group/credit/establish/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface DetailCompareRequest {
  /**
   * 集团授信立项id
   */
  groupCreditEstablishId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [集团授信立项-基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10944) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /group/credit/establish/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [集团授信评审-基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10948) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /group/credit/review/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface DetailCompareRequest {
  /**
   * 集团授信评审id
   */
  groupCreditReviewId: number
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [集团授信评审-基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/10948) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /group/credit/review/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:59`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [项目评审保理报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2716) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/factoring/price/detail/compare`
 * @更新时间 `2022-08-08 17:43:13`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [项目评审保理报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2716) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/factoring/price/detail/compare`
 * @更新时间 `2022-08-08 17:43:13`
 */
export type DetailCompareResponse = {
  diffData?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}[]

/**
 * 接口 [项目评审债权转让报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2720) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/aoc/price/detail/compare`
 * @更新时间 `2022-08-08 17:43:14`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [项目评审债权转让报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2720) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/aoc/price/detail/compare`
 * @更新时间 `2022-08-08 17:43:14`
 */
export type DetailCompareResponse = {
  diffData?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}[]

/**
 * 接口 [项目评审基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2712) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareRequest {
  /**
   * project review Id
   */
  id?: number
  /**
   * 流程ID
   */
  processInstanceId?: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [项目评审基本信息-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2712) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/base/info/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareResponse {
  KEY?: {
    beforeValue?: {}
    value?: {}
    isChange?: boolean
  }
}

/**
 * 接口 [项目评审报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/3084) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/price/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareRequest {
  /**
   * 所属的projectId
   */
  projectId?: number
  /**
   * 流程id
   */
  processInstanceId?: string
  /**
   * 版本号
   */
  version?: string
}

/**
 * 接口 [项目评审报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/3084) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/price/detail/compare`
 * @更新时间 `2023-01-13 15:17:58`
 */
export interface DetailCompareResponse {
  /**
   * 租赁报价方案
   */
  leasePriceDetailRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
  /**
   * 保理报价方案
   */
  factoringPriceDetailRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
  /**
   * 债权转让报价方案
   */
  aocPriceDetailRSP?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}

/**
 * 接口 [项目评审租赁报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2724) 的 **请求类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/lease/price/detail/compare`
 * @更新时间 `2022-08-08 17:43:14`
 */
export interface DetailCompareRequest {
  /**
   * id
   */
  id?: number
}

/**
 * 接口 [项目评审租赁报价方案-对比↗](http://api-dev.zswl.cn:3011/project/11/interface/api/2724) 的 **返回类型**
 *
 * @分类 [编辑区数据版本对比-接口↗](http://api-dev.zswl.cn:3011/project/11/interface/api/cat_414)
 * @请求头 `POST /proj/review/lease/price/detail/compare`
 * @更新时间 `2022-08-08 17:43:14`
 */
export type DetailCompareResponse = {
  diffData?: {
    KEY?: {
      beforeValue?: {}
      value?: {}
      isChange?: boolean
    }
  }
}[]

/* prettier-ignore-end */
