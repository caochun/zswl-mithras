import { http } from '@zswl/admin'

export default {
  getModelList: (data) => http.post('/flow/model/list', data),
  del: (data) => http.post('/flow/model/delete', data),
  publish: (data) => http.post('/flow/model/deploy', data),
  save: (data) =>
    http.post('/flow/model/save', data, {
      transformResult: (res) => res.data,
    }),
  getDetail: (data) => http.post('/flow/model/detail', data),
  getModuleConfig: (data) => http.post('/flow/model/modelConfig', data),
  searchFounder: (params) =>
    http.get('/select/founder', {
      params,
      headers: {
        functionCode: 'selectfounder-3',
      },
    }),
}
