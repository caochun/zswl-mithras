/* prettier-ignore-start */
import * as Types from './interface/riskCardInfoApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改评分卡基本信息
  postInfoModify: (data: Types.InfoModifyRequest): Promise<Types.InfoModifyResponse> =>
    http.post('/risk/control/score/card/base/info/modify', data, { mock }),

  // 删除评分卡基本信息
  postInfoRemove: (data: Types.InfoRemoveRequest): Promise<Types.InfoRemoveResponse> =>
    http.post('/risk/control/score/card/base/info/remove', data, { mock }),

  // 新增评分卡基本信息
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/risk/control/score/card/base/info/add', data, { mock }),

  // 评分卡基本信息列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/risk/control/score/card/base/info/list', data, { mock }),

  // 评分卡基本信息详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/risk/control/score/card/base/info/detail', data, { mock }),
  // 预览
  getScoreDownload: (params: { year: string }): Promise<any> =>
    http('/risk/control/score/card/downLoad', { mock, params }),
}

/* prettier-ignore-end */
