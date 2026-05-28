/* prettier-ignore-start */
import * as Types from './interface/lprPricingApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // LPR定价列表
  postPricingList: (data: Types.PricingListRequest): Promise<Types.PricingListResponse> =>
    http.post('/new/ftp/lpr/pricing/list', data, { mock }),

  // 修改LPR定价
  postPricingModify: (data: Types.PricingModifyRequest): Promise<Types.PricingModifyResponse> =>
    http.post('/new/ftp/lpr/pricing/modify', data, { mock }),

  // 新增LPR定价
  postPricingAdd: (data: Types.PricingAddRequest): Promise<Types.PricingAddResponse> =>
    http.post('/new/ftp/lpr/pricing/add', data, { mock }),
}

/* prettier-ignore-end */
