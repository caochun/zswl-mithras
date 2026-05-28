/* prettier-ignore-start */
import * as Types from './interface/material'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 合同文本管理-修改单个文件的签约方式
  postWaySingle: (data: Types.SigningwaySingleRequest): Promise<Types.SigningwaySingleResponse> =>
    http.post('/contract/text/manage/update/signing/way/single', data, { mock }),

  // 合同文本管理-修改默认的签约方式
  postWayDefault: (
    data: Types.SigningwayDefaultRequest
  ): Promise<Types.SigningwayDefaultResponse> =>
    http.post('/contract/text/manage/update/signing/way/default', data, { mock }),

  // 合同文本管理-单个客户用印
  postSingleSign: (data: Types.ManagesingleSignRequest): Promise<Types.ManagesingleSignResponse> =>
    http.post('/contract/text/manage/single/sign', data, { mock }),

  // 合同文本管理-台账列表
  postManageList: (data: Types.TextmanageListRequest): Promise<Types.TextmanageListResponse> =>
    http.post('/contract/text/manage/list', data, { mock }),

  // 合同文本管理-合同文本下载
  postDownloadAll: (
    data: Types.ManagedownloadAllRequest
  ): Promise<Types.ManagedownloadAllResponse> =>
    http.post('/contract/text/manage/download/all', data, { mock, type: 'download', timeout: 0 }),

  // 合同文本管理-合同签署照片和视频
  postAndVideos: (data: Types.PhotosandVideosRequest): Promise<Types.PhotosandVideosResponse> =>
    http.post('/contract/text/manage/sign/photos/and/videos', data, { mock }),

  // 合同文本管理-已签约详情
  postSignedDetail: (
    data: Types.ManagesignedDetailRequest
  ): Promise<Types.ManagesignedDetailResponse> =>
    http.post('/contract/text/manage/signed/detail', data, { mock }),

  // 合同文本管理-待签约批量下载
  postWaitSign: (data: Types.DownloadwaitSignRequest): Promise<Types.DownloadwaitSignResponse> =>
    http.post('/contract/text/manage/download/wait/sign', data, {
      mock,
      type: 'download',
      timeout: 0,
    }),

  // 合同文本管理-批量用印
  postBatchSign: (data: Types.ManagebatchSignRequest): Promise<Types.ManagebatchSignResponse> =>
    http.post('/contract/text/manage/batch/sign', data, {
      mock,
      transformResult: (res) => res.data,
      timeout: 0,
    }),

  // 合同文本管理-未签约详情
  postUnSignedDetail: (
    data: Types.ManageunSignedDetailRequest
  ): Promise<Types.ManageunSignedDetailResponse> =>
    http.post('/contract/text/manage/unSigned/detail', data, { mock }),
}

/* prettier-ignore-end */
