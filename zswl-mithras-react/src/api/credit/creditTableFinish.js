import { http } from '@zswl/admin'

export default {
  getBatchList: (params) => http.post('/cr/batch/incre/list', params),
  getAccountList: (params, header) => http.post('/cr/account/list', params, header),
  exportExcel: (params) => http.post('/cr/export/excel', params, { type: 'download', timeout: 0 }),
}
