/* prettier-ignore-start */
import * as Types from './interface/guaranteeManage'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 修改担保信息
  postInfoModify: (data: Types.InfoModifyRequest): Promise<Types.InfoModifyResponse> =>
    http.post('/fund/guarantee/info/modify', data, { mock, type: 'upload' }),

  // 删除担保信息
  postInfoRemove: (data: Types.InfoRemoveRequest): Promise<Types.InfoRemoveResponse> =>
    http.post('/fund/guarantee/info/remove', data, { mock }),

  // 担保信息列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/fund/guarantee/info/list', data, { mock }),

  // 担保信息详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/fund/guarantee/info/detail', data, { mock }),

  // 新增担保信息
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/fund/guarantee/info/add', data, { mock, type: 'upload' }),
}

/* prettier-ignore-end */
