import { http } from '@zswl/admin'

export default {
  saveExecution: (params) => http.post('/flow/execution/cc', params),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),
}
