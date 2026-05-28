import { http } from '@zswl/admin'

export default {
  getModelList: (data) => http.post('/flow/model/list', data),
  del: (data) => http.post('/flow/model/delete', data),
  publish: (data) => http.post('/flow/model/deploy', data),
 
}
