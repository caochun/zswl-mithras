import { http } from '@zswl/admin'

export default {
  fund: {
    getList: (params) => http.post('/documentManagementLedger/fund/list/query', params),
    batchDownload: (params) => http.post('/documentManagementLedger/fund/batch/download', params),
    downloadRecordsQuery: (params) =>
      http.post('/documentManagementLedger/fund/download/records/query', params),
    fileDownload: (params) =>
      http.post('/documentManagementLedger/fund/file/download', params, { type: 'download' }),
  },
  project: {
    getList: (params) => http.post('/documentManagementLedger/proj/list/query', params),
  },
  detail: {
    getOperationsDirDict: (id) => http.post('/fund/filingMaterial/getOperationsDirDict', { id }),
    download: (params) =>
      http.get('/fund/filingMaterial/download', { params, type: 'download', timeout: 0 }),
    batchDownload: (params) =>
      http.post(
        '/fund/filingMaterial/batchDownload',
        {
          ...params,
          type: 'download',
          timeout: 0,
        },
        { type: 'download' }
      ),
  },
}
