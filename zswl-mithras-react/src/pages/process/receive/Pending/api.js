import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/flow/task/myReceive/todo/list', params),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),

  batchPass: (body) => http.post('/flow/execution/batch/pass', body),
}
