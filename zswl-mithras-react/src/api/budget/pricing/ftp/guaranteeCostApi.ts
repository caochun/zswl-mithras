/* prettier-ignore-start */
import * as Types from './interface/guaranteeCostApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改担保成本刷新
  postPricingFlash: (data: Types.PricingFlashRequest): Promise<Types.PricingFlashResponse> =>
    http.post('/new/ftp/guarantee/cost/pricing/flash', data, { mock }),

  // 修改担保成本定价
  postPricingModify: (data: Types.PricingModifyRequest): Promise<Types.PricingModifyResponse> =>
    http.post('/new/ftp/guarantee/cost/pricing/modify', data, { mock }),

  // 担保成本定价列表
  postPricingList: (data: Types.PricingListRequest): Promise<Types.PricingListResponse> =>
    http.post('/new/ftp/guarantee/cost/pricing/list', data, { mock }),
}

/* prettier-ignore-end */
