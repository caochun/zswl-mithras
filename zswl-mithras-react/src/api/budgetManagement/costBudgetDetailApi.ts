/* prettier-ignore-start */
import * as Types from './interface/costBudgetDetailApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改预算管理-预算计划-成本预算-明细
  postDetailModify: (data: Types.DetailModifyRequest): Promise<Types.DetailModifyResponse> =>
    http.post('/budget/plan/cost/detail/modify', data, { mock }),

  // 删除预算管理-预算计划-成本预算-明细
  postDetailRemove: (data: Types.DetailRemoveRequest): Promise<Types.DetailRemoveResponse> =>
    http.post('/budget/plan/cost/detail/remove', data, { mock }),

  // 预算管理-预算计划-成本预算-明细列表
  postDetailList: (data: Types.DetailListRequest): Promise<Types.DetailListResponse> =>
    http.post('/budget/plan/cost/detail/list', data, { mock }),
}

/* prettier-ignore-end */
