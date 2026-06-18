/* prettier-ignore-start */
import * as Types from './interface/newFtpQuarterlyBasePricingApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改季度指导基础定价
  postPricingModify: (data: Types.PricingModifyRequest): Promise<Types.PricingModifyResponse> =>
    http.post('/new/ftp/quarterly/base/pricing/modify', data, { mock }),

  // 季度指导基础定价列表
  postPricingDetail: (data: Types.PricingDetailRequest): Promise<Types.PricingDetailResponse> =>
    http.post('/new/ftp/quarterly/base/pricing/detail', data, { mock }),
}

/* prettier-ignore-end */
