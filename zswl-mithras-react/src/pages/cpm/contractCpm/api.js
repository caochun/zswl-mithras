import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/contractcp/list', params),

  //导出
  exportList: (params) =>
    http.post('/contractcp/list/export', params, {
      type: 'download',
      timeout: 0,
    }),

  //租金支付通知书
  pushRentNotify: (params) =>
    http.post('/contractcp/list/pushRentNotify', params,),

  postLetterExport: (params) =>
    http.get('/collection/letter/export', {
      params,
      type: 'download',
      timeout: 0,
      fileName: '往来账项对账函.xlsx',
    }),
  postLetterList: (params) => http.post('/collection/letter/list', params),
}
