/* prettier-ignore-start */
import * as Types from './interface/listLibraryApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 批量查询下载模版
  getTemplateDownload: (
    params: Types.TemplateDownloadRequest
  ): Promise<Types.TemplateDownloadResponse> =>
    http.get('/black/gray/batch/query/template/download', {
      params,
      mock,
      type: 'download',
      fileName: '批量查询下载模板',
    }),

  // 黑灰名单综合查询导出
  getInfoExport: (params: Types.InfoExportRequest): Promise<Types.InfoExportResponse> =>
    http.get('/black/gray/base/info/export', {
      params,
      mock,
      type: 'download',
      fileName: '黑灰名单综合查询记录.xlsx',
    }),

  // \/black\/gray\/batch\/batch\/query\/
  postQuery: (data: Types.BatchQueryRequest): Promise<Types.BatchQueryResponse> =>
    http.post('/black/gray/batch/batch/query/', data, { mock }),

  // 批量查询
  postBatchQuery: (data: Types.BatchQueryRequest): Promise<Types.BatchQueryResponse> =>
    http.post('/black/gray/batch/batch/query', data, { mock, type: 'upload' }),

  // 风控系统查询可突破黑灰名单类型
  postBreakBusiness: (data: Types.BreakBusinessRequest): Promise<Types.BreakBusinessResponse> =>
    http.post('/black/gray/can/break/business', data, { mock }),

  // 风控系统查询风险规模
  postEnterpriseRiskScale: (
    data: Types.EnterpriseRiskScaleRequest
  ): Promise<Types.EnterpriseRiskScaleResponse> =>
    http.post('/black/gray/enterprise/riskScale', data, { mock }),

  // 风控系统查询黑灰名单库列表
  postInfoList: (data: Types.InfoListRequest): Promise<Types.InfoListResponse> =>
    http.post('/black/gray/base/info/list', data, { mock }),

  // 风控系统查询黑灰名单库按企业汇总列表
  postInfoDistinct: (data: Types.InfoDistinctRequest): Promise<Types.InfoDistinctResponse> =>
    http.post('/black/gray/base/info/distinct', data, { mock }),

  // 风控系统查询黑灰名单库按企业汇总列表导出
  getDistinctExport: (params: Types.DistinctExportRequest): Promise<Types.DistinctExportResponse> =>
    http.get('/black/gray/base/info/distinct/export', { params, mock, type: 'download' }),

  // 风控系统查询黑灰名单库机构下列表
  postOrgList: (data: Types.OrgListRequest): Promise<Types.OrgListResponse> =>
    http.post('/black/gray/org/list', data, { mock }),

  // 风控系统查询黑灰名单库详情
  postInfoDetail: (data: Types.InfoDetailRequest): Promise<Types.InfoDetailResponse> =>
    http.post('/black/gray/base/info/detail', data, { mock }),
}

/* prettier-ignore-end */
