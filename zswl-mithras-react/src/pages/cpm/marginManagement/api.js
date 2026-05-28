import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/margin/list', params),

  //导出
  exportList: (params) =>
    http.post('/margin/list/export', params, {
      type: 'download',
      timeout: 0,
    }),
}
