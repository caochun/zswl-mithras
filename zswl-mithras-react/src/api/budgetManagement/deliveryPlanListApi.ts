/* prettier-ignore-start */
import * as Types from './interface/deliveryPlanListApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 预算管理-预算计划-投放计划-信息
  postPayInfo: (data: Types.PayInfoRequest): Promise<Types.PayInfoResponse> =>
    http.post('/budget/plan/pay/info', data, { mock }),

  // 预算管理-预算计划-投放计划-列表
  postPayPageList: (data: Types.PayPageListRequest): Promise<Types.PayPageListResponse> =>
    http.post('/budget/plan/pay/pageList', data, { mock }),
}

/* prettier-ignore-end */
