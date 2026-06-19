import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contractcp/list', params),

  exportList: (params) =>
    http.post('/contractcp/list/export', params, {
      type: 'download',
      timeout: 0,
    }),

  pushRentNotify: (params) => http.post('/contractcp/list/pushRentNotify', params),

  postLetterExport: (params) =>
    http.get('/collection/letter/export', {
      params,
      type: 'download',
      timeout: 0,
      fileName: '往来账项对账函.xlsx',
    }),
  postLetterList: (params) => http.post('/collection/letter/list', params),

  contractDetail: (params) => http.post('/contractcp/contract/detail', params),
  cashDetail: (params) => http.post('/contractcp/cash/detail', params),
  cashList: (params) => http.post('/contractcp/cash/list', params),
  contractList: (params) => http.post('/contractcp/contract/list', params),
  contractDetailList: (params) => http.post('/contractcp/cash/detail', params),

  cashDetailList: (params) =>
    http.post('/contractcp/cash/detail/export', params, {
      type: 'download',
      fileName: '现金流明细.xlsx',
      timeout: 0,
    }),
}
