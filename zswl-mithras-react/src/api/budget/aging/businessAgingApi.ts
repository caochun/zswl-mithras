/* prettier-ignore-start */
import * as Types from './interface/businessAgingApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 关闭帐龄主表
  postInfoClose: (data: Types.InfoCloseRequest): Promise<Types.InfoCloseResponse> =>
    http.post('/finance/account/age/base/info/close', data, { mock }),

  // 删除帐龄主表
  postInfoRemove: (data: Types.InfoRemoveRequest): Promise<Types.InfoRemoveResponse> =>
    http.post('/finance/account/age/base/info/remove', data, { mock }),

  // 帐龄主表-完成
  postInfoEffect: (data: Types.InfoEffectRequest): Promise<Types.InfoEffectResponse> =>
    http.post('/finance/account/age/base/info/effect', data, { mock }),

  // 帐龄主表列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/finance/account/age/base/info/list', data, { mock }),

  // 帐龄主表详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/finance/account/age/base/info/detail', data, { mock }),

  // 新增帐龄主表
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/finance/account/age/base/info/add', data, { mock, timeout: 0 }),
}

/* prettier-ignore-end */
