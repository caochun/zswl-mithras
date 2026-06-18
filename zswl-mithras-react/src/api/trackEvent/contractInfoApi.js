import { http } from '@zswl/admin'

export default {
  postProjList: (params) =>
    http.post('/contract/review/query', params, {
      headers: {
        functionCode: 'contractreviewquery_trackevent',
      },
    }),
}
