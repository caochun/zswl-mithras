import { http } from '@zswl/admin'

export default {
  submit: (params) => http.post('/cr/submit', params),
  sync: (params) => http.post('/cr/sync', params),
  getBatchNumber: (params) => http.post('/cr/get/batch/number', params),
  getBatchReason: (params) => http.post('/cr/batch/reason', params),
  exportExcel: (params) => http.post('/cr/export/excel', params, { type: 'download', timeout: 0 }),
}
