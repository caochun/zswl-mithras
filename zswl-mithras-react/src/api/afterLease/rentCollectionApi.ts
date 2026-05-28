/* prettier-ignore-start */
import * as Types from './interface/rentCollectionApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 租金催收罚息减免-列表
  postReductionList: (data: Types.ReductionListRequest): Promise<Types.ReductionListResponse> =>
    http.post('/rent/collection/penalty/reduction/list', data, { mock }),

  // 租金催收罚息减免-变更
  postPenaltyModify: (data: Types.PenaltyModifyRequest): Promise<Types.PenaltyModifyResponse> =>
    http.post('/rent/collection/penalty/modify', data, { mock }),

  // 租金催收罚息减免-提交审批
  postPenaltyEffect: (data: Types.PenaltyEffectRequest): Promise<Types.PenaltyEffectResponse> =>
    http.post('/rent/collection/penalty/effect', data, {
      mock,
      // type: 'upload',
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    }),

  // 租金催收首页列表
  postIndexList: (data: Types.IndexListRequest): Promise<Types.IndexListResponse> =>
    http.post('/rent/collection/index/list', data, { mock }),
}

/* prettier-ignore-end */
