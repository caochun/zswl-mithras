import { http } from '@zswl/admin'

export default {
  getList: (data) => http.post('/stampDuty/list', data),
  getContractList: (data) => http.post('/stampDuty/contract/list', data),
  download: (data) => http.post('/stampDuty/export', data, { type: 'download' }),
  downloadTemplate: (templateName, moduleType) =>
    http.get(`/file/download/template?templateName=${templateName}&moduleType=${moduleType}`, {
      type: 'download',
      headers: { functionCode: 'stampDutyTemplateDownload' },
    }),
  upload: (data) =>
    http.post('/stampDuty/import', data, {
      type: 'upload',
      transformResult: (res) => res.data,
      timeout: 0,
    }),
  delete: (data) => http.post('/stampDuty/delete', data),
  add: (data) => http.post('/stampDuty/add', data),
}
