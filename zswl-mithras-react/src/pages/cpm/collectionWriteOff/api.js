import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/collection/list', params),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        headers: {
          functionCode: 'clientlist-4',
        },
      }
    ),
  exportList: (params) =>
    http.post('/collection/list/export', params, {
      type: 'download',
      timeout: 0,
    }),
}
