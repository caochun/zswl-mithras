import { http } from '@zswl/admin'

export default {
  visitSummary: (params) => http.post('/app/pc/visit/summary', params, {}),
  visitDetail: (params) => http.post('/app/pc/visit/list', params, {}),
  appVisitQueryCompany: (params) => http.post('/app/visit/queryCompany', params),
  appFileExport: (params) =>
    http.post('/file/export', params, {
      type: 'download',
      timeout: 0,
      headers: {
        functionCode: 'apppcFileExport',
      },
    }),
  getVisitRecordFileList: (params) =>
    http.post('/file/list', params, {
      headers: {
        functionCode: 'visitRecordFileList',
      },
    }),
  downloadVisitFiles: (params) =>
    http.post('/app/pc/visit/file/download', params, {
      fileName: '拜访照片.zip',
      type: 'download',
      timeout: 0,
    }),
  downloadVisitRecordFiles: (params) =>
    http.post('/app/pc/visit/file/download', params, {
      fileName: '拜访照片.zip',
      type: 'download',
      headers: {
        functionCode: 'visitRecordFileList',
      },
    }),
}
