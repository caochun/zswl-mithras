/* prettier-ignore-start */
import * as Types from './interface/editableCompare'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // ftp季度指导-基本报价-对比
  postPricingCompare: (data: Types.PricingCompareRequest): Promise<Types.PricingCompareResponse> =>
    http.post('/ftp/quarterly/base/pricing/compare', data, { mock }),

  // ftp季度指导-客户主体计价-对比
  postQuarterlyCustomerPricingCompare: (
    data: Types.PricingCompareRequest
  ): Promise<Types.PricingCompareResponse> =>
    http.post('/ftp/quarterly/customer/pricing/compare', data, { mock }),

  // ftp季度指导-按企业报价-对比
  postQuarterlyEnterprisePricingCompare: (
    data: Types.PricingCompareRequest
  ): Promise<Types.PricingCompareResponse> =>
    http.post('/ftp/quarterly/enterprise/pricing/compare', data, { mock }),

  // ftp季度指导-按月报价-对比
  postQuarterlyMonthPricingCompare: (
    data: Types.PricingCompareRequest
  ): Promise<Types.PricingCompareResponse> =>
    http.post('/ftp/quarterly/month/pricing/compare', data, { mock }),

  // ftp月度指导-pricing-对比
  postMonthlyPricingCompare: (
    data: Types.PricingCompareRequest
  ): Promise<Types.PricingCompareResponse> =>
    http.post('/ftp/monthly/pricing/compare', data, { mock }),

  // ftp月度指导-valuation-对比
  postValuationCompare: (
    data: Types.ValuationCompareRequest
  ): Promise<Types.ValuationCompareResponse> =>
    http.post('/ftp/monthly/valuation/compare', data, { mock }),

  // ftp月度指导-对比
  postDetailCompare: (data: Types.DetailCompareRequest): Promise<Types.DetailCompareResponse> =>
    http.post('/ftp/monthly/guidance/detail/compare', data, { mock }),

  // 文件列表分组比对
  postListGroupCompare: (data: Types.DetailCompareRequest): Promise<Types.DetailCompareResponse> =>
    http.post('/file/list/group/compare/v2', data, { mock }),
  // 文件列表
  postFileListCompare: (data: Types.DetailCompareRequest): Promise<Types.DetailCompareResponse> =>
    http.post('/file/list/compare', data, { mock }),
}

/* prettier-ignore-end */
