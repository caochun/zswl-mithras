/* prettier-ignore-start */
import * as Types from './interface/trackingApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // \/trackEvent\/testEffect
  getTrackEventTestEffect: (
    params: Types.TrackEventTestEffectRequest,
  ): Promise<Types.TrackEventTestEffectResponse> =>
    http.get('/trackEvent/testEffect', { params, mock }),

  // 合同编号下拉框
  getTrackEventContractCodeList: (
    params: Types.TrackEventContractCodeListRequest,
  ): Promise<Types.TrackEventContractCodeListResponse> =>
    http.get('/trackEvent/contractCodeList', { params, mock }),

  // 处理人下拉框
  getTrackEventQueryProcessor: (
    params: Types.TrackEventQueryProcessorRequest,
  ): Promise<Types.TrackEventQueryProcessorResponse> =>
    http.get('/trackEvent/queryProcessor', { params, mock }),

  // 批量导出
  postTrackEventDownload: (
    data: Types.TrackEventDownloadRequest,
  ): Promise<Types.TrackEventDownloadResponse> =>
    http.post('/trackEvent/download', data, { mock, type: 'download' }),

  // 项目名称下拉框
  getTrackEventProjNameList: (
    params: Types.TrackEventProjNameListRequest,
  ): Promise<Types.TrackEventProjNameListResponse> =>
    http.get('/trackEvent/projNameList', { params, mock }),

  // 信息回显
  postTrackEventContractInfo: (
    data: Types.TrackEventContractInfoRequest,
  ): Promise<Types.TrackEventContractInfoResponse> =>
    http.post('/trackEvent/contractInfo', data, { mock }),

  // 关闭任务
  getTrackEventClose: (
    params: Types.TrackEventCloseRequest,
  ): Promise<Types.TrackEventCloseResponse> => http.get('/trackEvent/close', { params, mock }),

  // 跟踪事项列表
  postTrackEventList: (data: Types.TrackEventListRequest): Promise<Types.TrackEventListResponse> =>
    http.post('/trackEvent/list', data, { mock }),

  // 跟踪事项新增
  postTrackEventAdd: (data: Types.TrackEventAddRequest): Promise<Types.TrackEventAddResponse> =>
    http.post('/trackEvent/add', data, { mock }),

  // 跟踪事项编辑
  postTrackEventUpdate: (
    data: Types.TrackEventUpdateRequest,
  ): Promise<Types.TrackEventUpdateResponse> => http.post('/trackEvent/update', data, { mock }),

  // 跟踪事项详情
  getTrackEventDetail: (
    params: Types.TrackEventDetailRequest,
  ): Promise<Types.TrackEventDetailResponse> => http.get('/trackEvent/detail', { params, mock }),
}

/* prettier-ignore-end */
