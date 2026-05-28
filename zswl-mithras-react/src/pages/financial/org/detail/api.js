import { http } from '@zswl/admin'

export default {
  getDetail: (params) => http.get('/api/getDetail', { params }),
  create: (data) => http.post('/api/create', data),
  update: (data) => http.post('/api/update', data),
}
