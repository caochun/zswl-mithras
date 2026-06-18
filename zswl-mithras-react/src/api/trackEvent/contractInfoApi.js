import { http } from '@zswl/admin'

export default {
  postProjList: (params) =>
    http.post('/contract/review/query', params, {
      headers: {
        functionCode: 'contractreviewquery_trackevent',
      },
    }),
  getClientList: (params) =>
    http.post(
      '/client/list',
      { scene: 'query', ...params },
      {
        timeout: 0,
        headers: {
          functionCode: 'clientlist-trackevent',
        },
      }
    ),
}
