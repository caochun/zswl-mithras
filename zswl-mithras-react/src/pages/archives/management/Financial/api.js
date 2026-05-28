import { http } from '@zswl/admin'

export default {
  // 资金端归档资料-列表查询
  getList: (params) => http.post('/documentManagementLedger/fund/list/query', params),

  // 资金端归档资料-批量下载文件（异步）
  batchDownload: (params) => http.post('/documentManagementLedger/fund/batch/download', params),

  // 资金端归档资料-下载记录查询
  downloadRecordsQuery: (params) => http.post('/documentManagementLedger/fund/download/records/query', params),

  // 资金端归档资料-文件下载
  fileDownload: (params) =>
    http.post('/documentManagementLedger/fund/file/download', params, { type: 'download' }),
}
