/* prettier-ignore-start */
import * as Types from './interface/riskCardTargetApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改评分卡指标
  postTargetModify: (data: Types.TargetModifyRequest): Promise<Types.TargetModifyResponse> =>
    http.post('/risk/control/score/card/target/modify', data, { mock }),

  // 删除评分卡指标
  postTargetRemove: (data: Types.TargetRemoveRequest): Promise<Types.TargetRemoveResponse> =>
    http.post('/risk/control/score/card/target/remove', data, { mock }),

  // 新增评分卡指标
  postTargetAdd: (data: Types.TargetAddRequest): Promise<Types.TargetAddResponse> =>
    http.post('/risk/control/score/card/target/add', data, { mock }),

  // 查询指标名称
  postTargetSearch: (data: Types.TargetSearchRequest): Promise<Types.TargetSearchResponse> =>
    http.post('/risk/control/score/card/target/search', data, { mock }),

  // 评分卡指标列表
  postTargetList: (data: Types.TargetListRequest): Promise<Types.TargetListResponse> =>
    http.post('/risk/control/score/card/target/list', data, { mock }),
}

/* prettier-ignore-end */
