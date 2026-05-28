/* prettier-ignore-start */
import * as Types from './interface/fundReceiptRepayCashFlowApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改本金利息一览表
  postFlowModify: (data: Types.FlowModifyRequest): Promise<Types.FlowModifyResponse> =>
    http.post('/fund/receipt/repay/cash/flow/modify', data, { mock }),

  // 本金利息一览表列表
  postFlowList: (data: Types.FlowListRequest): Promise<Types.FlowListResponse> =>
    http.post('/fund/receipt/repay/cash/flow/list', data, { mock }),

  // 本金利息一览表列表
  postFlowListCompare: (data: Types.FlowListRequest): Promise<Types.FlowListResponse> =>
    http.post('/fund/receipt/repay/cash/flow/list/compare', data, { mock }),
}

/* prettier-ignore-end */
