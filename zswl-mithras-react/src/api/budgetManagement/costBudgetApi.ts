/* prettier-ignore-start */
import * as Types from './interface/costBudgetApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改预算管理-预算计划-成本预算
  postCostModify: (data: Types.CostModifyRequest): Promise<Types.CostModifyResponse> =>
    http.post('/budget/plan/cost/modify', data, { mock }),

  // 删除预算管理-预算计划-成本预算
  postCostRemove: (data: Types.CostRemoveRequest): Promise<Types.CostRemoveResponse> =>
    http.post('/budget/plan/cost/remove', data, { mock }),

  // 预算管理-预算计划-成本预算列表
  postCostList: (data: Types.CostListRequest): Promise<Types.CostListResponse> =>
    http.post('/budget/plan/cost/list', data, { mock }),
}

/* prettier-ignore-end */
