import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/process/prepare/list', params),
  cancelProcess: (params) => http.post('/process/prepare/discard', params),
  submitProcess: (params) => http.post('/process/prepare/commit', params),
  checkFile: (params) => http.post('/fund/filingMaterial/checkFile', params),
  postProjectdistributionSubmit: (data) =>
    http.post('/finance/projectdistribution/submit', data, { mock: false }),
}
