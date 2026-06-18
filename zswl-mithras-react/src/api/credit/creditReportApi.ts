/* prettier-ignore-start */
import * as Types from './interface/creditReportApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 征信查询批量导出
  postBaseExport: (data: Types.BaseExportRequest): Promise<Types.BaseExportResponse> =>
    http.post('/creditreport/base/export', data, { mock, type: 'download' }),

  // 客户比对承租人及担保人工商信息
  postCompareBusiness: (
    data: Types.CompareBusinessRequest,
  ): Promise<Types.CompareBusinessResponse> =>
    http.post('/creditreport/base/creditSearch/client/compare/business', data, { mock }),

  // 征信报告查询列表
  postBaseList: (data: Types.BaseListRequest): Promise<Types.BaseListResponse> =>
    http.post('/creditreport/base/list', data, { mock }),

  // 征信报告查询提交
  postBaseSubmit: (data: Types.BaseSubmitRequest): Promise<Types.BaseSubmitResponse> =>
    http.post('/creditreport/base/submit', data, { mock }),

  // 征信报告查询详情
  getBaseDetail: (params: Types.BaseDetailRequest): Promise<Types.BaseDetailResponse> =>
    http.get('/creditreport/base/detail', { params, mock }),

  // 征信报告查询详情保存
  postBaseSave: (data: Types.BaseSaveRequest): Promise<Types.BaseSaveResponse> =>
    http.post('/creditreport/base/save', data, { mock }),
  postCreditReportSelectFileUpload: (data: any): Promise<any> =>
    http.post('/file/upload', data, {
      mock,
      type: 'upload',
      transformResult: (res) => res.data,
      timeout: 0,
      headers: {
        functionCode: 'creditReportSelectFileUpload',
      },
    }),

  // 征信查询删除
  getBaseDelete: (params: Types.BaseDeleteRequest): Promise<Types.BaseDeleteResponse> =>
    http.get('/creditreport/base/delete', { params, mock }),

  // 新增征信报告查询
  postBaseAdd: (data: Types.BaseAddRequest): Promise<Types.BaseAddResponse> =>
    http.post('/creditreport/base/add', data, { mock }),

  // 查询有征信报告查询权限的客户列表信息
  getBaseClientInfo: (params: Types.BaseClientInfoRequest): Promise<Types.BaseClientInfoResponse> =>
    http.get('/creditreport/base/clientInfo', { params, mock }),

  // 根据客户id反显客户信息和关联项目信息
  getBaseShowCreditReportByClientId: (
    params: Types.BaseShowCreditReportByClientIdRequest,
  ): Promise<Types.BaseShowCreditReportByClientIdResponse> =>
    http.get('/creditreport/base/showCreditReportByClientId', { params, mock }),
}

/* prettier-ignore-end */
