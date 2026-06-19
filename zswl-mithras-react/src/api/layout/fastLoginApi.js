import { http } from '@zswl/admin'

export default {
  getUserList: (params) => http.get('/user/list', { params }),
  getAuthCode: (data) => http.post('/user/getAuthCode', data, { type: 'formData' }),
  login: (data) => http.post('/user/login', data, { type: 'formData' }),
}
