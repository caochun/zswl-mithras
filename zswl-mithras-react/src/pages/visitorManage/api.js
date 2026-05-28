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
}
