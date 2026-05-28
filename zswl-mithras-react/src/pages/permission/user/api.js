import { http } from '@zswl/admin'

export default {
  getList: (params) => http.get('/user/list', { params }),
  remove: (params) => http.delete('/user', { params }),
  update: (data) => http.put('/user', data),
  create: (data) => http.post('/user', data),
  getJobList: (params) => http.get('/dictionary/type/list', { params }),
  getOrg: () => http.get('/org'),
  changeStatus: (data) => http.put('/user/status', data, { type: 'formData' }),
  resetPassword: (data) => http.put('/user/resetPwd', data, { type: 'formData' }),
  sync: (data) => http.post('/sync/mainCode', data),
  getUserPermissions: (params) => http.get('/user/permissions', { params }),
}
