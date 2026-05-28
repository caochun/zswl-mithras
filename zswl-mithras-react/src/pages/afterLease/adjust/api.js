import { http } from '@zswl/admin'

export default {
  getList: (params) => http.post('/after/lease/adjust/info/list', params),
  addInfo: (params) =>
    http.post('/after/lease/adjust/info/add', params, {
      transformResult: (res) => res.data,
    }),

  getProjList: (params) =>
    http.post('/contract/review/query', params, {
      headers: {
        functionCode: 'contractreviewquery_afterleaseadjust',
      },
    }),
}
