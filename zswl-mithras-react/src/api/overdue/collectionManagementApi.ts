/* prettier-ignore-start */
import * as Types from './interface/collectionManagementApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // \/test\/overdueClientInfoUpdateTask
  getTestOverdueClientInfoUpdateTask: (
    params: Types.TestOverdueClientInfoUpdateTaskRequest,
  ): Promise<Types.TestOverdueClientInfoUpdateTaskResponse> =>
    http.get('/test/overdueClientInfoUpdateTask', { params, mock }),

  // 催收动作列表导出
  postActionDownload: (data: Types.ActionDownloadRequest): Promise<Types.ActionDownloadResponse> =>
    http.post('/overduecollection/action/download', data, { mock, type: 'download' }),

  // 合同列表导出
  postContractExport: (data: Types.ContractExportRequest): Promise<Types.ContractExportResponse> =>
    http.post('/overduecollection/contract/export', data, { mock, type: 'download' }),

  // 修改催收信息
  postActionUpdate: (data: Types.ActionUpdateRequest): Promise<Types.ActionUpdateResponse> =>
    http.post('/overduecollection/action/update', data, { mock }),

  // 催收列表
  postOverduecollectionList: (
    data: Types.OverduecollectionListRequest,
  ): Promise<Types.OverduecollectionListResponse> =>
    http.post('/overduecollection/list', data, { mock }),

  // 催收动作详情
  postActionDetail: (data: Types.ActionDetailRequest): Promise<Types.ActionDetailResponse> =>
    http.post('/overduecollection/action/detail', data, { mock }),

  // 催收提交审批
  postActionSubmit: (data: Types.ActionSubmitRequest): Promise<Types.ActionSubmitResponse> =>
    http.post('/overduecollection/action/submit', data, { mock }),

  // 催收详情
  postOverduecollectionDetail: (
    data: Types.OverduecollectionDetailRequest,
  ): Promise<Types.OverduecollectionDetailResponse> =>
    http.post('/overduecollection/detail', data, { mock }),

  // 合同下拉列表
  getContractPulldown: (
    params: Types.ContractPulldownRequest,
  ): Promise<Types.ContractPulldownResponse> =>
    http.get('/overduecollection/contract/pulldown', { params, mock }),

  // 合同列表
  getContractList: (params: Types.ContractListRequest): Promise<Types.ContractListResponse> =>
    http.get('/overduecollection/contract/list', { params, mock }),

  // 新增催收动作
  postActionAdd: (data: Types.ActionAddRequest): Promise<Types.ActionAddResponse> =>
    http.post('/overduecollection/action/add', data, { mock }),

  // 生成函件
  postLetterGenerate: (data: Types.LetterGenerateRequest): Promise<Types.LetterGenerateResponse> =>
    http.post('/overduecollection/letter/generate', data, { mock }),

  postActionDelete: (data) =>
    http.post('/overduecollection/action/delete', data, { mock }),
}

/* prettier-ignore-end */
