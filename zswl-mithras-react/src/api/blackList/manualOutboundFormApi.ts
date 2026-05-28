/* prettier-ignore-start */
import * as Types from './interface/manualOutboundFormApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 黑灰名单出库导出
  getRecordExport: (params: Types.RecordExportRequest): Promise<Types.RecordExportResponse> =>
    http.get('/black/gray/manual/outbound/record/export', { params, mock, type: 'download' }),

  // 修改黑灰名单人工出库表
  postOutboundModify: (data: Types.OutboundModifyRequest): Promise<Types.OutboundModifyResponse> =>
    http.post('/black/gray/manual/outbound/modify', data, { mock }),

  // 新增黑灰名单人工出库表
  postOutboundAdd: (data: Types.OutboundAddRequest): Promise<Types.OutboundAddResponse> =>
    http.post('/black/gray/manual/outbound/add', data, { mock }),

  // 黑灰名单人工出库表列表
  postOutboundList: (data: Types.OutboundListRequest): Promise<Types.OutboundListResponse> =>
    http.post('/black/gray/manual/outbound/list', data, { mock }),

  // 黑灰名单人工出库表删除
  postOutboundRemove: (data: Types.OutboundRemoveRequest): Promise<Types.OutboundRemoveResponse> =>
    http.post('/black/gray/manual/outbound/remove', data, { mock }),

  // 黑灰名单人工出库表详情
  postOutboundDetail: (data: Types.OutboundDetailRequest): Promise<Types.OutboundDetailResponse> =>
    http.post('/black/gray/manual/outbound/detail', data, { mock }),
}

/* prettier-ignore-end */
