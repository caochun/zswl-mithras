/* prettier-ignore-start */
import * as Types from './interface/provisioning'
import { http } from '@zswl/admin'

const mock = false
// const mock = { mode: 2 }
export default {
  // 导出绩效-拨备详情
  exportDetail: (params: any): Promise<any> =>
    http.get('/kpi/provision/detail/export', { params, type: 'download', timeout: 0 }),

  // 修改绩效-拨备表
  postInfoModify: (data: Types.InfoModifyRequest): Promise<Types.InfoModifyResponse> =>
    http.post('/kpi/provision/base/info/modify', data, { mock }),

  // 刷新绩效-拨备表
  postInfoRefresh: (data: Types.InfoRefreshRequest): Promise<Types.InfoRefreshResponse> =>
    http.post('/kpi/provision/base/info/refresh', data, { mock }),

  // 新增绩效-拨备表
  postInfoAdd: (data: Types.InfoAddRequest): Promise<Types.InfoAddResponse> =>
    http.post('/kpi/provision/base/info/add', data, { mock }),

  // 确认绩效-拨备表
  postInfoEffect: (data: Types.InfoEffectRequest): Promise<Types.InfoEffectResponse> =>
    http.post('/kpi/provision/base/info/effect', data, { mock }),

  // 绩效-拨备表列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/kpi/provision/base/info/list', data, { mock }),

  // 绩效详情-拨备表
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/kpi/provision/base/info/detail', data, { mock }),
}

/* prettier-ignore-end */
