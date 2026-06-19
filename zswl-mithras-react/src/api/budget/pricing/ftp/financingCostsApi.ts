/* prettier-ignore-start */
import * as Types from './interface/financingCostsApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  postDraftPricingFlash: (data: any): Promise<any> =>
    http.post('/new/ftp/financing/cost/pricing/draft/flash', data, { mock }),

  postDraftDetail: (data: any): Promise<any> =>
    http.post('/new/ftp/financing/cost/draft/detail', data, { mock }),

  postDraftPricingModify: (data: any): Promise<any> =>
    http.post('/new/ftp/financing/cost/pricing/draft/modify', data, { mock }),

  // 修改融资成本定价
  postPricingModify: (data: Types.PricingModifyRequest): Promise<Types.PricingModifyResponse> =>
    http.post('/new/ftp/financing/cost/pricing/modify', data, { mock }),

  // 融资成本定价列表
  postPricingList: (data: Types.PricingListRequest): Promise<Types.PricingListResponse> =>
    http.post('/new/ftp/financing/cost/pricing/list', data, { mock }),

  // 融资成本定价刷新
  postPricingFlash: (data: Types.PricingFlashRequest): Promise<Types.PricingFlashResponse> =>
    http.post('/new/ftp/financing/cost/pricing/flash', data, { mock }),
}

/* prettier-ignore-end */
