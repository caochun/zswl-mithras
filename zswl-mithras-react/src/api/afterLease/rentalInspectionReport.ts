/* prettier-ignore-start */
import * as Types from './interface/rentalInspectionReport'
import { http } from '@zswl/admin'

const mock = false
// const mock= { delay: 800 }
export default {
  // 下载指定的单个项目报告
  postSingleDownload: (
    params: Types.SingleDownloadRequest
  ): Promise<Types.SingleDownloadResponse> =>
    http.get('/afterlease/check/project/report/single/download', {
      params,
      mock,
      type: 'download',
      timeout: 0,
    }),

  // 下载指定的批量项目报告
  postBatchDownload: (params: Types.BatchDownloadRequest): Promise<Types.BatchDownloadResponse> =>
    http.get('/afterlease/check/project/report/batch/download', {
      params,
      type: 'download',
      timeout: 0,
    }),

  // 下载指定部门的项目报告
  postDeptDownload: (params: Types.DeptDownloadRequest): Promise<Types.DeptDownloadResponse> =>
    http.get('/afterlease/check/project/report/dept/download', { params, type: 'download' }),

  // 上传非公用事业类型报告的检查附件
  postFileUpload: (data: Types.FileUploadRequest): Promise<Types.FileUploadResponse> =>
    http.post('/afterlease/check/project/report/nonpublic/file/upload', data, {
      mock,
      type: 'upload',
      timeout: 0,
      transformResult: (res) => res.data,
    }),

  // 保存客户财务报表快照数据
  postSnapshotSave: (data: Types.SnapshotSaveRequest): Promise<Types.SnapshotSaveResponse> =>
    http.post('/afterlease/check/project/report/finance/snapshot/save', data, { mock }),

  // 保存非公用事业补充说明信息
  postExtraSave: (data: Types.ExtraSaveRequest): Promise<Types.ExtraSaveResponse> =>
    http.post('/afterlease/check/project/report/nonpublic/extra/save', data, { mock }),

  // 保存项目检查报告基本信息
  postBaseSave: (data: Types.BaseSaveRequest): Promise<Types.BaseSaveResponse> =>
    http.post('/afterlease/check/project/report/base/save', data, { mock }),

  // 保存项目检查报告检查内容
  postContentSave: (data: Types.ContentSaveRequest): Promise<Types.ContentSaveResponse> =>
    http.post('/afterlease/check/project/report/content/save', data, { mock }),

  // 保存项目检查报告检查总结
  postSummarySave: (data: Types.SummarySaveRequest): Promise<Types.SummarySaveResponse> =>
    http.post('/afterlease/check/project/report/summary/save', data, { mock }),

  // 获取客户财务报表快照数据
  postFinanceSnapshot: (
    data: Types.FinanceSnapshotRequest
  ): Promise<Types.FinanceSnapshotResponse> =>
    http.post('/afterlease/check/project/report/finance/snapshot', data, { mock }),

  // 获取检查报告中用户上传的财务数据文件
  postFinanceList: (data: Types.FinanceListRequest): Promise<Types.FinanceListResponse> =>
    http.post('/afterlease/check/project/report/file/finance/list', data, { mock }),

  // 获取非公用事业类型报告的检查附件列表
  postFileList: (data: Types.FileListRequest): Promise<Types.FileListResponse> =>
    http.post('/afterlease/check/project/report/nonpublic/file/list', data, { mock }),

  // 获取非公用事业补充说明信息
  postExtraGet: (data: Types.ExtraGetRequest): Promise<Types.ExtraGetResponse> =>
    http.post('/afterlease/check/project/report/nonpublic/extra/get', data, { mock }),

  // 获取项目检查报告基本信息
  postBaseGet: (data: Types.BaseGetRequest): Promise<Types.BaseGetResponse> =>
    http.post('/afterlease/check/project/report/base/get', data, { mock }),

  // 获取项目检查报告检查内容
  postContentGet: (data: Types.ContentGetRequest): Promise<Types.ContentGetResponse> =>
    http.post('/afterlease/check/project/report/content/get', data, { mock }),

  // 获取项目检查报告检查总结
  postSummaryGet: (data: Types.SummaryGetRequest): Promise<Types.SummaryGetResponse> =>
    http.post('/afterlease/check/project/report/summary/get', data, { mock }),
}

/* prettier-ignore-end */
