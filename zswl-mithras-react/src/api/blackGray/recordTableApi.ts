/* prettier-ignore-start */
import * as Types from './interface/recordTableApi'
import { http } from '@zswl/admin'

const mock = false
// const mock= { mode:2 }
export default {
  // 黑灰名单上传模版下载
  getTemplateDownload: (
    params: Types.TemplateDownloadRequest
  ): Promise<Types.TemplateDownloadResponse> =>
    http.get('/black/gray/warehouse/record/upload/template/download', {
      params,
      mock,
      type: 'download',
      fileName: '黑灰名单批量录入模板',
    }),

  // 黑灰名单导出
  getRecordExport: (params: Types.RecordExportRequest): Promise<Types.RecordExportResponse> =>
    http.get('/black/gray/warehouse/record/export', { params, mock, type: 'download' }),

  // 上传黑灰名单记录表
  postRecordUpload: (data: Types.RecordUploadRequest): Promise<Types.RecordUploadResponse> =>
    http.post('/black/gray/warehouse/record/upload', data, { mock, type: 'upload' }),

  // 业务类型树
  postTypeList: (data: Types.TypeListRequest): Promise<Types.TypeListResponse> =>
    http.post('/black/gray/business/type/list', data, { mock }),

  // 修改黑灰名单记录表
  postRecordModify: (data: Types.RecordModifyRequest): Promise<Types.RecordModifyResponse> =>
    http.post('/black/gray/warehouse/record/modify', data, { mock }),

  // 批量新增黑灰名单记录表
  postBatchAdd: (data: Types.BatchAddRequest): Promise<Types.BatchAddResponse> =>
    http.post('/black/gray/warehouse/record/batch/add', data, { mock }),

  // 批量补全黑灰名单集团信息
  postBatchModify: (data: Types.BatchModifyRequest): Promise<Types.BatchModifyResponse> =>
    http.post('/black/gray/warehouse/record/batch/modify', data, { mock }),

  // 新增黑灰名单记录表
  postRecordAdd: (data: Types.RecordAddRequest): Promise<Types.RecordAddResponse> =>
    http.post('/black/gray/warehouse/record/add', data, { mock }),

  // 解析黑灰名单上传记录表
  postAnalysisUpload: (data: Types.AnalysisUploadRequest): Promise<Types.AnalysisUploadResponse> =>
    http.post('/black/gray/warehouse/analysis/upload', data, { mock, type: 'upload' }),

  // 黑灰名单记录表列表
  postRecordList: (data: Types.RecordListRequest): Promise<Types.RecordListResponse> =>
    http.post('/black/gray/warehouse/record/list', data, { mock }),

  // 黑灰名单记录表删除
  postRecordDelete: (data: Types.RecordDeleteRequest): Promise<Types.RecordDeleteResponse> =>
    http.post('/black/gray/warehouse/record/delete', data, { mock }),

  // 黑灰名单记录表详情
  postRecordDetail: (data: Types.RecordDetailRequest): Promise<Types.RecordDetailResponse> =>
    http.post('/black/gray/warehouse/record/detail', data, { mock }),
}

/* prettier-ignore-end */
